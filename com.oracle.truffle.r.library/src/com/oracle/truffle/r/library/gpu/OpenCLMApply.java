/*
 * Copyright (c) 2013, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.r.library.gpu;

import java.util.ArrayList;

import uk.ac.ed.accelerator.common.GraalAcceleratorOptions;
import uk.ac.ed.accelerator.profiler.Profiler;
import uk.ac.ed.accelerator.profiler.ProfilerType;
import uk.ac.ed.datastructures.common.AcceleratorPArray;
import uk.ac.ed.datastructures.common.PArray;
import uk.ac.ed.datastructures.interop.InteropTable;
import uk.ac.ed.datastructures.interop.Interoperable;
import uk.ac.ed.jpai.graal.GraalGPUCompilationUnit;
import uk.ac.ed.jpai.graal.GraalGPUCompiler;
import uk.ac.ed.jpai.graal.GraalGPUExecutor;
import uk.ac.ed.marawacc.compilation.MarawaccGraalIR;
import uk.ac.ed.marawacc.graal.CompilerUtils;

import com.oracle.graal.nodes.StructuredGraph;
import com.oracle.graal.truffle.OptimizedCallTarget;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.r.library.gpu.cache.CacheGPUExecutor;
import com.oracle.truffle.r.library.gpu.cache.InternalGraphCache;
import com.oracle.truffle.r.library.gpu.cache.RCacheObjects;
import com.oracle.truffle.r.library.gpu.cache.RFunctionMetadata;
import com.oracle.truffle.r.library.gpu.cache.RGPUCache;
import com.oracle.truffle.r.library.gpu.exceptions.MarawaccExecutionException;
import com.oracle.truffle.r.library.gpu.options.ASTxOptions;
import com.oracle.truffle.r.library.gpu.phases.FilterInterpreterNodes;
import com.oracle.truffle.r.library.gpu.phases.scope.ScopeData;
import com.oracle.truffle.r.library.gpu.types.TypeInfo;
import com.oracle.truffle.r.library.gpu.types.TypeInfoList;
import com.oracle.truffle.r.library.gpu.utils.ASTxUtils;
import com.oracle.truffle.r.library.gpu.utils.ASTxUtils.ScopeVarInfo;
import com.oracle.truffle.r.nodes.builtin.RExternalBuiltinNode;
import com.oracle.truffle.r.nodes.function.FunctionDefinitionNode;
import com.oracle.truffle.r.runtime.RRuntime;
import com.oracle.truffle.r.runtime.context.Engine.ParseException;
import com.oracle.truffle.r.runtime.context.RContext;
import com.oracle.truffle.r.runtime.data.RArgsValuesAndNames;
import com.oracle.truffle.r.runtime.data.RFunction;
import com.oracle.truffle.r.runtime.data.RVector;
import com.oracle.truffle.r.runtime.data.model.RAbstractVector;

/**
 * AST Node to check the connection with Marawacc. This is just a proof of concept.
 */
public final class OpenCLMApply extends RExternalBuiltinNode {

    private static final String R_EVAL_DESCRIPTION = "<eval>";
    private static final boolean ISTRUFFLE = true;
    private static int iteration = 0;

    ArrayList<com.oracle.graal.graph.Node> scopedNodes;

    /**
     * Given the {@link StructuredGraph}, this method invokes the OpenCL code generation. We also
     * need some meta-data to generate the code such as the input array (in {@link PArray} form, and
     * the first value (runtime object) to generate the output for the kernel template.
     *
     * @param inputPArray
     * @param callTarget
     * @param graphToCompile
     * @param firstValue
     * @return {@link GraalGPUCompilationUnit}
     */
    private GraalGPUCompilationUnit compileForMarawaccBackend(PArray<?> inputPArray, OptimizedCallTarget callTarget, StructuredGraph graphToCompile, Object firstValue, Interoperable interoperable,
                    Object[] lexicalScope, int nArgs) {

        ScopeData scopeData = ASTxUtils.scopeArrayConstantDetection(graphToCompile);

        if (lexicalScope != null) {
            scopeData.setData(lexicalScope);
        }
        GraalAcceleratorOptions.printOffloadKernel = true;

        if (ASTxOptions.debug) {
            scopedNodes = ASTxUtils.applyCompilationPhasesForOpenCLAndDump(graphToCompile);
        } else {
            scopedNodes = ASTxUtils.applyCompilationPhasesForOpenCL(graphToCompile);
        }

        new FilterInterpreterNodes(6).apply(graphToCompile);
        CompilerUtils.dumpGraph(graphToCompile, "Filter");

        GraalGPUCompilationUnit gpuCompilationUnit = GraalGPUCompiler.compileGraphToOpenCL(inputPArray, graphToCompile, callTarget, firstValue, ISTRUFFLE, interoperable, scopeData.getData(),
                        scopedNodes, nArgs);
        gpuCompilationUnit.setScopeArrays(scopeData.getData());
        gpuCompilationUnit.setScopeNodes(scopedNodes);

        // Insert graph into Truffle OpenCL Cache
        InternalGraphCache.INSTANCE.installGPUBinaryIntoCache(graphToCompile, gpuCompilationUnit);

        return gpuCompilationUnit;
    }

    /**
     * Given the {@link GraalGPUCompilationUnit}, this method executes the OpenCL code. It copies
     * the data to the device, runs the kernel and copies back the result.
     *
     * It returns an array list with one element, the result in Object format (PArray).
     *
     * @param inputPArray
     * @param graph
     * @param gpuCompilationUnit
     * @return {@link ArrayList}
     * @throws MarawaccExecutionException
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ArrayList<Object> runWithMarawaccAccelerator(PArray<?> inputPArray, StructuredGraph graph, GraalGPUCompilationUnit gpuCompilationUnit, RFunction function)
                    throws MarawaccExecutionException {
        GraalGPUExecutor executor = CacheGPUExecutor.INSTANCE.getExecutor(gpuCompilationUnit);
        if (executor == null) {
            executor = new GraalGPUExecutor();
            CacheGPUExecutor.INSTANCE.insert(gpuCompilationUnit, executor);
        }
        AcceleratorPArray copyToDevice = executor.copyToDevice(inputPArray, gpuCompilationUnit.getInputType());
        AcceleratorPArray executeOnTheDevice = executor.executeOnTheDevice(graph, copyToDevice, gpuCompilationUnit.getOuputType(), gpuCompilationUnit.getScopeArrays());
        PArray result = executor.copyToHost(executeOnTheDevice, gpuCompilationUnit.getOuputType());
        PArray<Integer> deopt = executor.getDeoptBuffer();
        if (deopt != null) {
            if (deopt.get(0) != 0) {
                System.out.println("DEOPT: " + deopt);
                throw new MarawaccExecutionException("Deoptimization", deopt.get(0));
            }
        }
        RGPUCache.INSTANCE.getCachedObjects(function).enableGPUExecution();
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(result);
        return arrayList;
    }

    private static ArrayList<Object> setOutput(Object firstValue) {
        ArrayList<Object> output = new ArrayList<>();
        output.add(firstValue);
        return output;
    }

    private static void checkFunctionInCache(RFunction function, RootCallTarget callTarget) {
        if (RGPUCache.INSTANCE.getCachedObjects(function).getIDExecution() == 0) {
            callTarget.generateIDForOpenCL();
            // Set the GPU execution to true;
            ((FunctionDefinitionNode) function.getRootNode()).setOpenCLFlag(true);
            RGPUCache.INSTANCE.getCachedObjects(function).incID();
        }
    }

    private static class JITMetaInput {
        private Object firstValue;
        private Interoperable interoperable;
        private Object[] lexicalScopes;
        private PArray<?> inputPArray;

        public JITMetaInput(Object firstValue, Interoperable interoperable, Object[] lexicalScopes, PArray<?> inputPArray) {
            super();
            this.firstValue = firstValue;
            this.interoperable = interoperable;
            this.lexicalScopes = lexicalScopes;
            this.inputPArray = inputPArray;
        }
    }

    private ArrayList<Object> checkAndRun(GraalGPUCompilationUnit gpuCompilationUnit, RootCallTarget callTarget, int index, JITMetaInput meta, RFunction function, int inputArgs)
                    throws MarawaccExecutionException {
        /*
         * Check if the graph is prepared for GPU compilation and invoke the compilation and
         * execution. On Stack Replacement (OSR): switch to compiled GPU code
         */
        StructuredGraph graphToCompile = MarawaccGraalIR.getInstance().getCompiledGraph(callTarget.getIDForOpenCL());
        if ((graphToCompile != null) && (gpuCompilationUnit == null)) {
            if (ASTxOptions.debug) {
                System.out.println("[MARAWACC-ASTX] Compiling the Graph to GPU - Iteration: " + index);
            }
            GraalGPUCompilationUnit oclCompileUnit = compileForMarawaccBackend(meta.inputPArray, (OptimizedCallTarget) callTarget, graphToCompile, meta.firstValue, meta.interoperable,
                            meta.lexicalScopes, inputArgs);
            return runWithMarawaccAccelerator(meta.inputPArray, graphToCompile, oclCompileUnit, function);
        }
        return null;
    }

    // Run in the interpreter and then JIT when the CFG is prepared for compilation
    private ArrayList<Object> runJavaOpenCLJIT(RAbstractVector input, RootCallTarget callTarget, RFunction function, int nArgs, RAbstractVector[] additionalArgs, String[] argsName,
                    Object firstValue, PArray<?> inputPArray, Interoperable interoperable, Object[] lexicalScopes, int argsOriginal) throws MarawaccExecutionException {

        checkFunctionInCache(function, callTarget);

        StructuredGraph graphToCompile = MarawaccGraalIR.getInstance().getCompiledGraph(callTarget.getIDForOpenCL());
        GraalGPUCompilationUnit gpuCompilationUnit = InternalGraphCache.INSTANCE.getGPUCompilationUnit(graphToCompile);

        if (graphToCompile != null && gpuCompilationUnit != null) {
            return runWithMarawaccAccelerator(inputPArray, graphToCompile, gpuCompilationUnit, function);
        }

        JITMetaInput meta = new JITMetaInput(firstValue, interoperable, lexicalScopes, inputPArray);
        ArrayList<Object> output = setOutput(firstValue);
        for (int i = 1; i < input.getLength(); i++) {
            Object[] argsPackage = ASTxUtils.createRArguments(nArgs, function, input, additionalArgs, argsName, i);
            Object value = callTarget.call(argsPackage);
            output.add(value);
            ArrayList<Object> checkAndRun = checkAndRun(gpuCompilationUnit, callTarget, i, meta, function, argsOriginal);
            if (checkAndRun != null) {
                return checkAndRun;
            }
        }
        return output;
    }

    // Run in the interpreter and then JIT when the CFG is prepared for compilation
    private ArrayList<Object> runJavaOpenCLJIT(PArray<?> input, RootCallTarget callTarget, RFunction function, int nArgs, PArray<?>[] additionalArgs, String[] argsName,
                    Object firstValue, PArray<?> inputPArray, Interoperable interoperable, Object[] lexicalScopes, int totalSize, int inputArgs) throws MarawaccExecutionException {

        checkFunctionInCache(function, callTarget);

        StructuredGraph graphToCompile = MarawaccGraalIR.getInstance().getCompiledGraph(callTarget.getIDForOpenCL());
        GraalGPUCompilationUnit gpuCompilationUnit = InternalGraphCache.INSTANCE.getGPUCompilationUnit(graphToCompile);

        if (graphToCompile != null && gpuCompilationUnit != null) {
            ArrayList<Object> runWithMarawaccAccelerator = runWithMarawaccAccelerator(inputPArray, graphToCompile, gpuCompilationUnit, function);
            return runWithMarawaccAccelerator;
        }

        JITMetaInput meta = new JITMetaInput(firstValue, interoperable, lexicalScopes, inputPArray);
        ArrayList<Object> output = setOutput(firstValue);

        for (int i = 1; i < totalSize; i++) {
            Object[] argsPackage = ASTxUtils.createRArguments(nArgs, function, input, additionalArgs, argsName, i);
            Object value = callTarget.call(argsPackage);
            output.add(value);
            ArrayList<Object> checkAndRun = checkAndRun(gpuCompilationUnit, callTarget, i, meta, function, inputArgs);
            if (checkAndRun != null) {
                return checkAndRun;
            }
        }
        return output;
    }

    // Run in the interpreter and then JIT when the CFG is prepared for compilation
    private static ArrayList<Object> runAfterDeopt(PArray<?> input, RootCallTarget callTarget, RFunction function, int nArgs, PArray<?>[] additionalArgs, String[] argsName,
                    Object firstValue, int totalSize) {
        checkFunctionInCache(function, callTarget);
        ArrayList<Object> output = setOutput(firstValue);
        for (int i = 1; i < totalSize; i++) {
            Object[] argsPackage = ASTxUtils.createRArguments(nArgs, function, input, additionalArgs, argsName, i);
            Object value = callTarget.call(argsPackage);
            output.add(value);
        }
        return output;
    }

    // Run in the interpreter and then JIT when the CFG is prepared for compilation
    private static ArrayList<Object> runAfterDeopt(RAbstractVector input, RootCallTarget callTarget, RFunction function, int nArgs, RAbstractVector[] additionalArgs, String[] argsName,
                    Object firstValue, int totalSize) {
        checkFunctionInCache(function, callTarget);
        ArrayList<Object> output = setOutput(firstValue);
        for (int i = 1; i < totalSize; i++) {
            Object[] argsPackage = ASTxUtils.createRArguments(nArgs, function, input, additionalArgs, argsName, i);
            Object value = callTarget.call(argsPackage);
            output.add(value);
        }
        return output;
    }

    /**
     * It checks if the function was already inserted into the cache. If it is that the case, we
     * return the metadata associated with the value such as the return value, parameters and
     * interoperable objects.
     *
     * @param input
     * @param function
     * @param additionalArgs
     * @return {@link RFunctionMetadata}.
     */
    private static RFunctionMetadata getCachedFunctionMetadata(PArray<?> input, RFunction function, PArray<?>[] additionalArgs) {
        if (RGPUCache.INSTANCE.getCachedObjects(function).getRFunctionMetadata() == null) {
            // Type inference -> execution of the first element
            int nArgs = ASTxUtils.getNumberOfArguments(function);
            String[] argsName = ASTxUtils.getArgumentsNames(function);
            Object[] argsPackage = ASTxUtils.createRArguments(nArgs, function, input, additionalArgs, argsName, 0);
            Object value = function.getTarget().call(argsPackage);

            // Inter-operable objects
            TypeInfo outputType = ASTxUtils.obtainTypeInfo(value);
            InteropTable interop = ASTxUtils.obtainInterop(outputType);

            Class<?>[] typeObject = ASTxUtils.createListSubTypes(interop, value);
            Interoperable interoperable = (interop != null) ? new Interoperable(interop, typeObject) : null;

            RFunctionMetadata metadata = new RFunctionMetadata(nArgs, argsName, argsPackage, value, outputType, interop, typeObject, interoperable);
            RGPUCache.INSTANCE.getCachedObjects(function).insertRFuctionMetadata(metadata);
            return metadata;
        } else {
            return RGPUCache.INSTANCE.getCachedObjects(function).getRFunctionMetadata();
        }
    }

    /**
     * It checks if the function was already inserted into the cache. If it is that the case, we
     * return the metadata associated with the value such as the return value, parameters and
     * interoperable objects.
     *
     * @param input
     * @param function
     * @param additionalArgs
     * @return {@link RFunctionMetadata}.
     */
    private static RFunctionMetadata getCachedFunctionMetadata(RAbstractVector input, RFunction function, RAbstractVector[] additionalArgs) {
        if (RGPUCache.INSTANCE.getCachedObjects(function).getRFunctionMetadata() == null) {
            // Type inference -> execution of the first element
            int nArgs = ASTxUtils.getNumberOfArguments(function);
            String[] argsName = ASTxUtils.getArgumentsNames(function);
            Object[] argsPackage = ASTxUtils.createRArguments(nArgs, function, input, additionalArgs, argsName, 0);
            Object value = function.getTarget().call(argsPackage);

            // Inter-operable objects
            TypeInfo outputType = ASTxUtils.obtainTypeInfo(value);
            InteropTable interop = ASTxUtils.obtainInterop(outputType);

            Class<?>[] typeObject = ASTxUtils.createListSubTypes(interop, value);
            Interoperable interoperable = (interop != null) ? new Interoperable(interop, typeObject) : null;

            RFunctionMetadata metadata = new RFunctionMetadata(nArgs, argsName, argsPackage, value, outputType, interop, typeObject, interoperable);
            RGPUCache.INSTANCE.getCachedObjects(function).insertRFuctionMetadata(metadata);
            return metadata;
        } else {
            return RGPUCache.INSTANCE.getCachedObjects(function).getRFunctionMetadata();
        }
    }

    private RAbstractVector computeOpenCLMApply(PArray<?> input, RFunction function, RootCallTarget target, PArray<?>[] additionalArgs, Object[] lexicalScopes, int numArgumentsOriginalFunction) {

        // Get the metadata from the cache
        RFunctionMetadata cachedFunctionMetadata = getCachedFunctionMetadata(input, function, additionalArgs);
        int nArgs = cachedFunctionMetadata.getnArgs();
        String[] argsName = cachedFunctionMetadata.getArgsName();
        Object value = cachedFunctionMetadata.getFirstValue();
        TypeInfo outputType = cachedFunctionMetadata.getOutputType();
        Interoperable interoperable = cachedFunctionMetadata.getInteroperable();

        int totalSize = ASTxUtils.getSize(input, additionalArgs);
        TypeInfoList inputTypeList = ASTxUtils.createTypeInfoListForInputWithPArrays(input, additionalArgs);

        // Marshal
        long startMarshal = System.nanoTime();
        PArray<?> inputPArrayFormat = ASTxUtils.createPArrays(input, additionalArgs, inputTypeList);
        long endMarshal = System.nanoTime();

        // Execution
        ArrayList<Object> result = null;
        long startExecution = System.nanoTime();
        try {
            result = runJavaOpenCLJIT(input, target, function, nArgs, additionalArgs, argsName, value, inputPArrayFormat, interoperable, lexicalScopes, totalSize, numArgumentsOriginalFunction);
        } catch (MarawaccExecutionException e) {
            // Deoptimization
            System.out.println("Running in the DEOPT mode");
            result = runAfterDeopt(inputPArrayFormat, target, function, nArgs, additionalArgs, argsName, value, totalSize);
        }
        long endExecution = System.nanoTime();

        // Get the result (un-marshal)
        boolean isGPUExecution = RGPUCache.INSTANCE.getCachedObjects(function).isGPUExecution();
        long startUnmarshal = System.nanoTime();
        RAbstractVector resultFastR = getResultFromPArray(isGPUExecution, outputType, result);
        long endUnmarshal = System.nanoTime();

        // Print profiler
        if (ASTxOptions.profileOpenCLASTx) {
            writeProfilerIntoBuffers(startMarshal, endMarshal, startExecution, endExecution, startUnmarshal, endUnmarshal);
        }
        return resultFastR;
    }

    private static void writeProfilerIntoBuffers(long startMarshal, long endMarshal, long startExecution, long endExecution, long startUnmarshal, long endUnmarshal) {
        // Marshal
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_MARSHAL, "start", startMarshal);
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_MARSHAL, "end", endMarshal);
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_MARSHAL, "end-start", (endMarshal - startMarshal));

        // Execution
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_EXECUTE, "start", startExecution);
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_EXECUTE, "end", endExecution);
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_EXECUTE, "end-start", (endExecution - startExecution));

        // Unmarshal
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_UNMARSHAL, "start", startUnmarshal);
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_UNMARSHAL, "end", endUnmarshal);
        Profiler.getInstance().writeInBuffer(ProfilerType.AST_R_UNMARSHAL, "end-start", (endUnmarshal - startUnmarshal));
    }

    @SuppressWarnings("deprecation")
    private static TypeInfoList createTypeInfoList(RAbstractVector input, RAbstractVector[] additionalArgs, int extraParams) {
        if (ASTxOptions.usePArrays) {
            return ASTxUtils.createTypeInfoListForInputWithPArrays(input, additionalArgs, extraParams);
        } else {
            return ASTxUtils.createTypeInfoListForInput(input, additionalArgs, extraParams);
        }
    }

    private RAbstractVector computeOpenCLMApply(RAbstractVector input, RFunction function, RootCallTarget target, RAbstractVector[] additionalArgs, Object[] lexicalScopes,
                    int numArgumentsOriginalFunction) {

        // Objects from cache
        RFunctionMetadata cachedFunctionMetadata = getCachedFunctionMetadata(input, function, additionalArgs);
        int nArgs = cachedFunctionMetadata.getnArgs();
        String[] argsName = cachedFunctionMetadata.getArgsName();
        Object value = cachedFunctionMetadata.getFirstValue();
        TypeInfo outputType = cachedFunctionMetadata.getOutputType();
        Interoperable interoperable = cachedFunctionMetadata.getInteroperable();

        // Get input types list
        int extraParams = nArgs - numArgumentsOriginalFunction;
        TypeInfoList inputTypeList = createTypeInfoList(input, additionalArgs, extraParams);

        // Marshal
        long startMarshal = System.nanoTime();
        PArray<?> inputPArray = ASTxUtils.createPArrays(input, additionalArgs, inputTypeList);
        long endMarshal = System.nanoTime();

        // Execution
        ArrayList<Object> result = null;
        long startExecution = System.nanoTime();
        try {
            result = runJavaOpenCLJIT(input, target, function, nArgs, additionalArgs, argsName, value, inputPArray, interoperable, lexicalScopes, numArgumentsOriginalFunction);
        } catch (MarawaccExecutionException e) {
            // Deoptimization
            if (ASTxOptions.debug) {
                System.out.println("Running in the DEOPT mode");
            }
            @SuppressWarnings("unused")
            int threadID = e.getThreadID();
            // Run sequentially -- TODO: Provide a mechanism that allows to restart the execution in
            // the correct place of the AST to specialise again
            result = runAfterDeopt(input, target, function, nArgs, additionalArgs, argsName, value, input.getLength());
        }
        long endExecution = System.nanoTime();

        // Get the result (un-marshal)
        boolean isGPUExecution = RGPUCache.INSTANCE.getCachedObjects(function).isGPUExecution();
        long startUnmarshal = System.nanoTime();
        RAbstractVector resultFastR = getResult(isGPUExecution, outputType, result);
        long endUnmarshal = System.nanoTime();

        // Print profiler information into buffer
        if (ASTxOptions.profileOpenCLASTx) {
            writeProfilerIntoBuffers(startMarshal, endMarshal, startExecution, endExecution, startUnmarshal, endUnmarshal);
        }
        return resultFastR;
    }

    @SuppressWarnings({"rawtypes", "deprecation"})
    private static RAbstractVector getResult(boolean wasExecutedOnGPU, TypeInfo outputType, ArrayList<Object> result) {
        if (!wasExecutedOnGPU) {
            // get the output in R-Type format
            return ASTxUtils.unMarshallResultFromArrayList(outputType, result);
        } else if (ASTxOptions.usePArrays) {
            // get the references
            return ASTxUtils.unMarshallFromFullPArrays(outputType, (PArray) result.get(0));
        } else if (ASTxOptions.newPArrayStrategy) {
            // Get the stored array reference
            return ASTxUtils.unmarshalFromPrimitivePArrays(outputType, (PArray) result.get(0));
        } else {
            // Real un-marshal
            return ASTxUtils.unMarshallResultFromPArrays(outputType, (PArray) result.get(0));
        }
    }

    @SuppressWarnings({"rawtypes"})
    private static RAbstractVector getResultFromPArray(boolean gpuExecution, TypeInfo outputType, ArrayList<Object> result) {
        if (!gpuExecution) {
            // get the output in R-Type format
            return ASTxUtils.unMarshallResultFromArrayList(outputType, result);
        } else {
            // get the references
            return ASTxUtils.unMarshallFromFullPArrays(outputType, (PArray) result.get(0));
        }
    }

    private static RFunction scopeRewritting(RFunction function, String[] scopeVars) {
        String newRewrittenFunction = ASTxUtils.rewriteFunction(function, scopeVars);
        Source newSourceFunction = Source.fromText(newRewrittenFunction, R_EVAL_DESCRIPTION).withMimeType(RRuntime.R_APP_MIME);
        try {
            RFunction newRFunction = (RFunction) RContext.getEngine().parseAndEval(newSourceFunction, false);
            return newRFunction;
        } catch (ParseException e) {
            throw new RuntimeException("[Fatal error] the function could not be rewritten");
        }
    }

    private RAbstractVector computeOpenCLMApplyForRVector(RArgsValuesAndNames args, boolean isRewritten, RVector[] vectors, Object[] lexicalScopes, RFunction function, RAbstractVector inputRArray,
                    RootCallTarget target, int numArgumentsOriginalFunction) {
        RAbstractVector mapResult = null;
        RAbstractVector[] additionalInputs = null;
        if (isRewritten) {
            additionalInputs = ASTxUtils.getAdditionalArguments(args, isRewritten, vectors, lexicalScopes.length);
        } else {
            additionalInputs = ASTxUtils.getRArrayWithAdditionalArguments(args);
        }
        mapResult = computeOpenCLMApply(inputRArray, function, target, additionalInputs, lexicalScopes, numArgumentsOriginalFunction);
        return mapResult;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object call(RArgsValuesAndNames args) {
        Profiler.getInstance().print("\nIteration: " + iteration++);

        long start = System.nanoTime();

        if (ASTxOptions.usePArrays) {
            RVector.WITH_PARRAYS = true;
        }

        Object firstParam = args.getArgument(0);
        RFunction function = (RFunction) args.getArgument(1);

        if (ASTxOptions.printAST) {
            ASTxUtils.printAST(function);
        }

        RAbstractVector inputRArray = null;
        PArray<?> parrayInput = null;
        boolean parrayFormat = false;

        if (firstParam instanceof RAbstractVector) {
            inputRArray = (RAbstractVector) firstParam;
        } else if (firstParam instanceof PArray) {
            parrayFormat = true;
            parrayInput = (PArray<?>) firstParam;
        } else {
            throw new RuntimeException("Vector or PArray expected, but " + firstParam.getClass() + " found");
        }

        RootCallTarget target = null;
        Object[] lexicalScopes = null;
        RVector[] vectors = null;
        String[] filterScopeVarNames = null;
        String[] scopeVars = null;

        // Get the callTarget from the cache
        if (!RGPUCache.INSTANCE.contains(function)) {
            // Lexical scoping from the AST level
            scopeVars = ASTxUtils.lexicalScopingAST(function);
            ScopeVarInfo valueOfScopeArrays = ASTxUtils.getValueOfScopeArrays(scopeVars, function);
            if (valueOfScopeArrays != null) {
                lexicalScopes = valueOfScopeArrays.getScopeVars();
                filterScopeVarNames = valueOfScopeArrays.getNameVars();
                vectors = valueOfScopeArrays.getVector();
            }
            RCacheObjects cachedObjects = new RCacheObjects(function.getTarget(), scopeVars, lexicalScopes);
            target = RGPUCache.INSTANCE.updateCacheObjects(function, cachedObjects);
        } else {
            target = RGPUCache.INSTANCE.getCallTarget(function);
            lexicalScopes = RGPUCache.INSTANCE.getCachedObjects(function).getLexicalScopeVars();
        }

        int numArgumentsOriginalFunction = ASTxUtils.getNumberOfArguments(function);
        boolean isRewritten = false;

        // Function rewriting for the scope variable detection
        if (ASTxOptions.scopeRewriting && (filterScopeVarNames != null)) {
            RFunction scopeRewritting = scopeRewritting(function, filterScopeVarNames);
            if (ASTxOptions.debug) {
                System.out.println("[DEBUG] New R Function rewritten => " + scopeRewritting.getRootNode().getSourceSection().getCode());
            }
            function = scopeRewritting;
            isRewritten = true;
            RCacheObjects cachedObjects = new RCacheObjects(function.getTarget(), scopeVars, lexicalScopes);
            target = RGPUCache.INSTANCE.updateCacheObjects(function, cachedObjects);
        }

        RAbstractVector mapResult = null;
        if (!parrayFormat) {
            mapResult = computeOpenCLMApplyForRVector(args, isRewritten, vectors, lexicalScopes, function, inputRArray, target, numArgumentsOriginalFunction);
        } else {
            PArray<?>[] additionalInputs = ASTxUtils.getPArrayWithAdditionalArguments(args);
            mapResult = computeOpenCLMApply(parrayInput, function, target, additionalInputs, lexicalScopes, numArgumentsOriginalFunction);
        }
        long end = System.nanoTime();

        if (scopedNodes != null) {
            scopedNodes.clear();
        }

        // Write profiler information into a buffer
        if (ASTxOptions.profileOpenCLASTx) {
            Profiler.getInstance().writeInBuffer("gpu start-end", (end - start));
            Profiler.getInstance().writeInBuffer("gpu start", start);
            Profiler.getInstance().writeInBuffer("gpu end", end);
        }

        return mapResult;
    }
}
