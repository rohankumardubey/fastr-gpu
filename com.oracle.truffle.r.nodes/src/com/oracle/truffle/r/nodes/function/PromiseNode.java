/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.nodes.function;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.CompilerDirectives.SlowPath;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import com.oracle.truffle.api.source.*;
import com.oracle.truffle.api.utilities.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.access.ConstantNode.ConstantMissingNode;
import com.oracle.truffle.r.options.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.RPromise.Closure;
import com.oracle.truffle.r.runtime.data.RPromise.EagerFeedback;
import com.oracle.truffle.r.runtime.data.RPromise.EvalPolicy;
import com.oracle.truffle.r.runtime.data.RPromise.PromiseProfile;
import com.oracle.truffle.r.runtime.data.RPromise.PromiseType;
import com.oracle.truffle.r.runtime.data.RPromise.RPromiseFactory;
import com.oracle.truffle.r.runtime.env.frame.*;

/**
 * This {@link RNode} implementations are used as a factory-nodes for {@link RPromise}s OR direct
 * evaluation of these {@link RPromise}s, depending on the promises {@link EvalPolicy}.<br/>
 * All these classes are created during/after argument matching and get cached afterwards, so they
 * get (and need to get) called every repeated call to a function with the same arguments.
 */
public abstract class PromiseNode extends RNode {
    /**
     * The {@link RPromiseFactory} which holds all information necessary to construct a proper
     * {@link RPromise} for every case that might occur.
     */
    protected final RPromiseFactory factory;

    protected final PromiseProfile promiseProfile = new PromiseProfile();

    /**
     * @param factory {@link #factory}
     */
    protected PromiseNode(RPromiseFactory factory) {
        this.factory = factory;
    }

    /**
     * @param src The {@link SourceSection} of the argument for debugging purposes
     * @param factory {@link #factory}
     * @return Depending on {@link RPromiseFactory#getEvalPolicy()} and
     *         {@link RPromiseFactory#getType()} the proper {@link PromiseNode} implementation
     */
    @SlowPath
    public static PromiseNode create(SourceSection src, RPromiseFactory factory) {
        assert factory.getType() != PromiseType.NO_ARG;

        PromiseNode pn = null;
        switch (factory.getEvalPolicy()) {
            case INLINED:
                if (factory.getType() == PromiseType.ARG_SUPPLIED) {
                    pn = new InlinedSuppliedPromiseNode(factory);
                } else {
                    pn = new InlinedPromiseNode(factory);
                }
                break;

            case PROMISED:
                // For ARG_DEFAULT, expr == defaultExpr!
                RNode expr = unfold(factory.getExpr());
                if (FastROptions.EagerEvalConstants.getValue() && isConstantArgument(expr)) {
                    // As Constants don't care where they are evaluated, we don't need to
                    // distinguish between ARG_DEFAULT and ARG_SUPPLIED
                    pn = new OptConstantPromiseNode(factory);
                    break;
                }

                if (factory.getType() == PromiseType.ARG_SUPPLIED) {
                    if (FastROptions.EagerEvalVariables.getValue() && isVariableArgument(expr)) {
                        pn = new OptVariablePromiseNode(factory, ((ReadVariableNode) expr).getSymbol());
                        break;
                    }

                    if (isVararg(expr)) {
                        pn = new VarargPromiseNode(factory, (VarArgNode) expr);
                        break;
                    }
                }

                pn = new PromisedNode(factory);
                break;

            default:
                throw new AssertionError();
        }

        pn.assignSourceSection(src);
        return pn;
    }

    private static RNode unfold(Object argObj) {
        RNode arg = (RNode) argObj;
        if (arg instanceof WrapArgumentNode) {
            return ((WrapArgumentNode) arg).getOperand();
        }
        return arg;
    }

    /**
     * This methods checks if an argument is a {@link ConstantNode}. Thanks to "..." unrolling, this
     * does not need to handle "..." as special case (which might result in a
     * {@link ConstantMissingNode} if empty), see {@link #isVararg(RNode)} for that.
     *
     * @param expr
     * @return Whether the given {@link RNode} is a {@link ConstantNode}
     */
    private static boolean isConstantArgument(RNode expr) {
        return expr instanceof ConstantNode;
    }

    /**
     * @param expr
     * @return Whether the given {@link RNode} is a {@link ReadVariableNode}
     *
     * @see FastROptions#EagerEvalVariables
     */
    private static boolean isVariableArgument(RNode expr) {
        return expr instanceof ReadVariableNode;
    }

    /**
     * This method checks whether to apply optimizations to RNodes created for single "..."
     * elements.
     *
     * @param expr
     * @return Whether the given {@link RNode} is a {@link VarArgNode}
     */
    private static boolean isVararg(RNode expr) {
        return expr instanceof VarArgNode;
    }

    /**
     * @param promise
     * @return Creates a {@link VarArgNode} for the given {@link RPromise}
     */
    public static VarArgNode createVarArg(RPromise promise) {
        VarArgNode result = new VarArgNode(promise);
        result.assignSourceSection(((RNode) promise.getRep()).getSourceSection());
        return result;
    }

    /**
     * A {@link PromiseNode} for supplied arguments
     */
    private static final class PromisedNode extends PromiseNode {

        private final ConditionProfile isSuppliedArgProfile = ConditionProfile.createBinaryProfile();

        private PromisedNode(RPromiseFactory factory) {
            super(factory);
        }

        @Override
        public Object execute(VirtualFrame frame) {
            MaterializedFrame execFrame = isSuppliedArgProfile.profile(factory.getType() == PromiseType.ARG_SUPPLIED) ? frame.materialize() : null;
            return factory.createPromise(execFrame);
        }
    }

    /**
     * A optimizing {@link PromiseNode}: It evaluates a constant directly.
     */
    private static final class OptConstantPromiseNode extends PromiseNode {

        @Child private RNode constantExpr;
        @CompilationFinal private boolean isEvaluated = false;
        @CompilationFinal private Object constant = null;

        private OptConstantPromiseNode(RPromiseFactory factory) {
            super(factory);
            this.constantExpr = (RNode) factory.getExpr();
        }

        /**
         * Creates a new {@link RPromise} every time.
         */
        @Override
        public Object execute(VirtualFrame frame) {
            if (!isEvaluated) {
                // Eval constant on first time and make it compile time constant afterwards
                CompilerDirectives.transferToInterpreterAndInvalidate();
                constant = constantExpr.execute(frame);
                isEvaluated = true;
            }

            return factory.createArgEvaluated(constant);
        }
    }

    /**
     * TODO Expand!
     */
    private static final class OptVariablePromiseNode extends PromiseNode implements EagerFeedback {
        @SuppressWarnings("unused") private final Symbol symbol;
        @Child private FrameSlotNode frameSlotNode;
        @Child private PromisedNode fallback = null;
        @Child private InlineCacheNode<VirtualFrame, RNode> inlineCache;

        public OptVariablePromiseNode(RPromiseFactory factory, Symbol symbol) {
            super(factory);
            this.symbol = symbol;
            this.frameSlotNode = FrameSlotNode.create(symbol.getName(), false);
            this.inlineCache = InlineCacheNode.createExpression(1); // As all PromiseNodes get
            // created for exactly one expr, 1 is sufficient
        }

        @Override
        public Object execute(VirtualFrame frame) {
            // If the frame slot we're looking for is not present yet, wait for it!
            if (!frameSlotNode.hasValue(frame)) {
                // We don't want to rewrite, as the the frame slot might show up later on
                return checkFallback().execute(frame);
            }
            FrameSlot slot = frameSlotNode.executeFrameSlot(frame);

            // Check if we may apply eager evaluation on this frame slot
            Assumption notChangedNonLocally = FrameSlotChangeMonitor.getMonitor(slot);
            if (!notChangedNonLocally.isValid()) {
                // Cannot apply optimizations, as the value to it got invalidated
                return rewriteToPromiseNode().execute(frame);
            }

            // Execute eagerly
            // The unfold is necessary to avoid the (fortunately only) side effect of
            // WrapArgumentNode that make vectors shared when read inside a Promise.
            // This works, as vectors are set to be "shared()" anyway once the EagerPromise gets
            // "really" evaluated
            RNode variableExpr = unfold(factory.getExpr());

            Object result = null;
            try {
                result = inlineCache.execute(frame, variableExpr);
            } catch (Throwable t) {
                // Errors are also side effects!
                // TODO Create EagerErrorPromise
                throw RInternalError.unimplemented();
            }

            // Create EagerPromise with the eagerly evaluated value under the assumption that the
            // value won't be altered until 1. read
            return factory.createEagerSuppliedPromise(result, notChangedNonLocally, RArguments.getDepth(frame), this);
        }

        private PromisedNode checkFallback() {
            if (fallback == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                insert(fallback = new PromisedNode(factory));
            }
            return fallback;
        }

        private PromiseNode rewriteToPromiseNode() {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            checkFallback();
            return replace(fallback);
        }

        @SlowPath
        public void onSuccess() {
// System.out.println("Successfully optimized read from '" + symbol.getName() + "'");
        }

        public void onFailure() {

        }
    }

    /**
     * TODO Expand!
     */
    private static final class VarargPromiseNode extends PromiseNode {
        @Child private VarArgNode varargNode;

        public VarargPromiseNode(RPromiseFactory factory, VarArgNode varargNode) {
            super(factory);
            this.varargNode = varargNode;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return factory.createVarargPromise(varargNode.getPromise());
        }
    }

    /**
     * This class is meant for supplied arguments (which have to be evaluated in the caller frame)
     * which are supposed to be evaluated {@link EvalPolicy#INLINED}: This means we can simply
     * evaluate it here, and as it's {@link EvalPolicy#INLINED}, return its value and not the
     * {@link RPromise} itself! {@link EvalPolicy#INLINED} {@link PromiseType#ARG_SUPPLIED}
     */
    private final static class InlinedSuppliedPromiseNode extends PromiseNode {
        @Child private RNode expr;
        @Child private InlineCacheNode<VirtualFrame, RNode> promiseExpressionCache = InlineCacheNode.createExpression(3);
        private final PromiseProfile promiseProfile = new PromiseProfile();

        private final BranchProfile isMissingProfile = new BranchProfile();
        private final BranchProfile isVarArgProfile = new BranchProfile();
        private final BranchProfile checkPromiseProfile = new BranchProfile();

        public InlinedSuppliedPromiseNode(RPromiseFactory factory) {
            super(factory);
            this.expr = (RNode) factory.getExpr();
        }

        @Override
        public Object execute(VirtualFrame frame) {
            // builtin.inline: We do re-evaluation every execute inside the caller frame, as we
            // know that the evaluation of default values has no side effects (has to be assured by
            // builtin implementations)
            Object obj = expr.execute(frame);
            if (obj == RMissing.instance) {
                isMissingProfile.enter();
                if (factory.getDefaultExpr() == null) {
                    return RMissing.instance;
                }
                RPromise promise = factory.createPromiseDefault();
                return PromiseHelper.evaluate(frame, promiseExpressionCache, promise, promiseProfile);
            } else if (obj instanceof RArgsValuesAndNames) {
                isVarArgProfile.enter();
                return ((RArgsValuesAndNames) obj).evaluate(frame, promiseProfile);
            } else {
                checkPromiseProfile.enter();
                return RPromise.checkEvaluate(frame, obj, promiseProfile);
            }
        }
    }

    /**
     * This class is meant for default arguments which have to be evaluated in the callee frame -
     * usually. But as this is for {@link EvalPolicy#INLINED}, arguments are simply evaluated inside
     * the caller frame: This means we can simply evaluate it here, and as it's
     * {@link EvalPolicy#INLINED}, return its value and not the {@link RPromise} itself!
     */
    private final static class InlinedPromiseNode extends PromiseNode {
        @Child private RNode defaultExpr;

        public InlinedPromiseNode(RPromiseFactory factory) {
            super(factory);
            // defaultExpr and expr are identical here!
            this.defaultExpr = (RNode) factory.getDefaultExpr();
        }

        @Override
        public Object execute(VirtualFrame frame) {
            // builtin.inline: We do re-evaluation every execute inside the caller frame, based on
            // the assumption that the evaluation of default values should have no side effects
            return defaultExpr.execute(frame);
        }
    }

    /**
     * This {@link RNode} is used to evaluate the expression given in a {@link RPromise} formerly
     * wrapped into a "..." after unrolling.<br/>
     * In a certain sense this is the class corresponding class for GNU R's PROMSXP (AST equivalent
     * of RPromise, only needed for varargs in FastR TODO Move to separate package together with
     * other varargs classes)
     */
    public final static class VarArgNode extends RNode {
        private final RPromise promise;
        @CompilationFinal private boolean isEvaluated = false;

        private final PromiseProfile promiseProfile = new PromiseProfile();

        private VarArgNode(RPromise promise) {
            this.promise = promise;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            // ExpressionExecutorNode would be overkill, as this is only executed once, and not in
            // the correct frame anyway
            if (!isEvaluated) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                Object result = promise.evaluate(frame, promiseProfile);
                isEvaluated = promise.isEvaluated(promiseProfile);
                return result;
            }

            // Should easily compile to constant
            return promise.getValue();
        }

        public RPromise getPromise() {
            return promise;
        }
    }

    /**
     * @param src
     * @param evalPolicy {@link EvalPolicy}
     * @param nodes The argument {@link RNode}s that got wrapped into this "..."
     * @param names The argument's names
     * @param callSrc The {@link SourceSection} of the call this "..." belongs to
     * @return Creates either a {@link InlineVarArgsPromiseNode} or a {@link VarArgsPromiseNode},
     *         depending on the {@link EvalPolicy}
     */
    @SlowPath
    public static RNode createVarArgs(SourceSection src, EvalPolicy evalPolicy, RNode[] nodes, String[] names, ClosureCache closureCache, SourceSection callSrc) {
        RNode node;
        switch (evalPolicy) {
            case INLINED:
                node = new InlineVarArgsPromiseNode(nodes, names);
                break;

            case PROMISED:
                node = new VarArgsPromiseNode(nodes, names, closureCache);
                break;

            default:
                throw new AssertionError();
        }

        node.assignSourceSection(src);
        return node;
    }

    /**
     * This class is used for wrapping arguments into "..." ({@link RArgsValuesAndNames}).
     */
    private final static class VarArgsPromiseNode extends RNode {
        protected final RNode[] nodes;
        protected final String[] names;
        protected final ClosureCache closureCache;

        public VarArgsPromiseNode(RNode[] nodes, String[] names, ClosureCache closureCache) {
            this.nodes = nodes;
            this.names = names;
            this.closureCache = closureCache;
        }

        @Override
        @ExplodeLoop
        public Object execute(VirtualFrame frame) {
            Object[] promises = new Object[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                Closure closure = closureCache.getOrCreateClosure(nodes[i]);
                promises[i] = RPromise.create(EvalPolicy.PROMISED, PromiseType.ARG_SUPPLIED, frame.materialize(), closure);
            }
            return new RArgsValuesAndNames(promises, names);
        }
    }

    /**
     * The {@link EvalPolicy#INLINED} counterpart of {@link VarArgsPromiseNode}: This gets a bit
     * more complicated, as "..." might also values from an outer "...", which might resolve to an
     * empty argument list.
     */
    public final static class InlineVarArgsPromiseNode extends RNode {
        @Children private final RNode[] varargs;
        protected final String[] names;

        private final PromiseProfile promiseProfile = new PromiseProfile();

        public InlineVarArgsPromiseNode(RNode[] nodes, String[] names) {
            this.varargs = nodes;
            this.names = names;
        }

        @Override
        @ExplodeLoop
        public Object execute(VirtualFrame frame) {
            Object[] evaluatedArgs = new Object[varargs.length];
            String[] evaluatedNames = names;
            int index = 0;
            for (int i = 0; i < varargs.length; i++) {
                Object argValue = varargs[i].execute(frame);
                if (argValue instanceof RArgsValuesAndNames) {
                    // this can happen if ... is simply passed around (in particular when the call
                    // chain contains two functions with just the ... argument)
                    RArgsValuesAndNames argsValuesAndNames = (RArgsValuesAndNames) argValue;
                    int newLength = evaluatedArgs.length + argsValuesAndNames.length() - 1;
                    if (newLength == 0) {
                        // Corner case: "f <- function(...) g(...); g <- function(...)"
                        // In this case, "..." gets evaluated, and its only content is "...", which
                        // itself is missing. Result: Both disappear!
                        evaluatedArgs[index++] = RMissing.instance;
                        continue;
                    }
                    evaluatedArgs = Utils.resizeArray(evaluatedArgs, newLength);
                    evaluatedNames = Utils.resizeArray(evaluatedNames, newLength);
                    Object[] varargValues = argsValuesAndNames.getValues();
                    for (int j = 0; j < argsValuesAndNames.length(); j++) {
                        evaluatedArgs[index] = RPromise.checkEvaluate(frame, varargValues[j], promiseProfile);
                        evaluatedNames[index] = argsValuesAndNames.getNames()[j];
                        index++;
                    }
                } else {
                    evaluatedArgs[index++] = RPromise.checkEvaluate(frame, argValue, promiseProfile);
                }
            }
            return new RArgsValuesAndNames(evaluatedArgs, evaluatedNames);
        }
    }
}
