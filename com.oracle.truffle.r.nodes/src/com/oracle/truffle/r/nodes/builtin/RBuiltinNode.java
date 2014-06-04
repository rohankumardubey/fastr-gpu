/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.nodes.builtin;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.builtin.base.*;
import com.oracle.truffle.r.nodes.function.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;

@NodeField(name = "builtin", type = RBuiltinFactory.class)
@NodeChild(value = "arguments", type = RNode[].class)
public abstract class RBuiltinNode extends RCallNode implements VisibilityController {

    public String getSourceCode() {
        return "<builtin>";
    }

    public abstract RNode[] getArguments();

    public abstract RBuiltinFactory getBuiltin();

    public Object[] getParameterNames() {
        return RArguments.EMPTY_OBJECT_ARRAY;
    }

    public RNode[] getParameterValues() {
        return RNode.EMTPY_RNODE_ARRAY;
    }

    @Override
    public final Object execute(VirtualFrame frame, RFunction function) {
        return execute(frame);
    }

    @Override
    public final int executeInteger(VirtualFrame frame, RFunction function) throws UnexpectedResultException {
        return executeInteger(frame);
    }

    @Override
    public final double executeDouble(VirtualFrame frame, RFunction function) throws UnexpectedResultException {
        return executeDouble(frame);
    }

    private static RNode[] createCallArguments(RBuiltinFactory builtin) {
        RNode[] args = new RNode[builtin.getFactory().getExecutionSignature().size()];
        int index = 0;
        int argIndex = 0;
        int total = builtin.getFactory().getExecutionSignature().size();
        for (int i = index; i < total; i++) {
            args[i] = new AccessArgumentNode(argIndex++, ConstantNode.create(RMissing.instance));
        }
        return args;
    }

    static RootCallTarget createArgumentsCallTarget(RBuiltinFactory builtin) {
        RNode[] args = createCallArguments(builtin);
        RBuiltinNode node = createNode(builtin, args, null);
        injectParameterDefaultValues(node);
        RBuiltinRootNode root = new RBuiltinRootNode(node, node.getParameterNames(), new FrameDescriptor());
        node.onCreate();
        return Truffle.getRuntime().createCallTarget(root);
    }

    private static void injectParameterDefaultValues(RBuiltinNode node) {
        RNode[] parameterValues = node.getParameterValues();
        RNode[] callArguments = node.getArguments();
        for (int i = 0; i < parameterValues.length; i++) {
            if (parameterValues[i] != null && i < callArguments.length && callArguments[i] instanceof AccessArgumentNode) {
                callArguments[i] = new AccessArgumentNode(i, parameterValues[i]);
            }
        }
    }

    public RCallNode inline(CallArgumentsNode args) {
        RNode[] builtinArguments;
        // static number of arguments
        builtinArguments = inlineStaticArguments(args);
        RBuiltinNode builtin = createNode(getBuiltin(), builtinArguments, args.getNameCount() == 0 ? null : args.getNames());
        builtin.onCreate();
        return builtin;
    }

    private static RBuiltinNode createNode(RBuiltinFactory factory, RNode[] builtinArguments, String[] argNames) {
        RBuiltin rBuiltin = null;
        GeneratedBy generatedBy = factory.getFactory().getClass().getAnnotation(GeneratedBy.class);
        if (generatedBy != null) {
            rBuiltin = generatedBy.value().getAnnotation(RBuiltin.class);
        }
        boolean isCombine = rBuiltin == null ? false : rBuiltin.isCombine();
        Object[] args = new Object[(isCombine ? 3 : 2) + factory.getConstantArguments().length];
        int index = 0;
        for (; index < factory.getConstantArguments().length; index++) {
            args[index] = factory.getConstantArguments()[index];
        }

        args[index++] = builtinArguments;
        args[index++] = factory;
        if (isCombine) {
            args[index++] = argNames;
        }

        return factory.getFactory().createNode(args);
    }

    protected RNode[] inlineStaticArguments(CallArgumentsNode args) {
        int signatureSize = getBuiltin().getFactory().getExecutionSignature().size();
        RNode[] children = new RNode[signatureSize];
        int index = 0;
        int argIndex = 0;

        RNode[] arguments = args.getArguments();
        RNode[] defaultValues = getParameterValues();
        for (int i = index; i < signatureSize; i++, argIndex++) {
            if (argIndex < arguments.length && arguments[argIndex] != null) {
                children[i] = arguments[argIndex];
            } else {
                children[i] = argIndex < defaultValues.length ? defaultValues[argIndex] : ConstantNode.create(RMissing.instance);
            }
        }
        return children;
    }

    /**
     * A wrapper builtin is a {@link RCustomBuiltinNode} that is able to create any arbitrary node
     * as builtin. It can be used as normal builtin. Implement {@link #createDelegate()} to create
     * that node. Warning: setting argument count is not yet implemented. set
     * {@link RBuiltin#lastParameterKind()} to varargs to get all arguments in a single node in the
     * arguments array.
     */
    // TODO support argument for number of arguments. Currently no arguments are passed
    // or in case of var args exactly one.
    public abstract static class RWrapperBuiltinNode extends RCustomBuiltinNode {

        @Child protected RNode delegate;

        public RWrapperBuiltinNode(RBuiltinNode prev) {
            super(prev);
        }

        protected abstract RNode createDelegate();

        @Override
        protected void onCreate() {
            delegate = insert(createDelegate());
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return delegate.execute(frame);
        }

        @Override
        public Object[] executeArray(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeArray(frame);
        }

        @Override
        public byte executeByte(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeByte(frame);
        }

        @Override
        public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeDouble(frame);
        }

        @Override
        public RFunction executeFunction(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeFunction(frame);
        }

        @Override
        public int executeInteger(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeInteger(frame);
        }

        @Override
        public RNull executeNull(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeNull(frame);
        }

        @Override
        public RDoubleVector executeRDoubleVector(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeRDoubleVector(frame);
        }

        @Override
        public RIntVector executeRIntVector(VirtualFrame frame) throws UnexpectedResultException {
            return delegate.executeRIntVector(frame);
        }

    }

    public static class RCustomBuiltinNode extends RBuiltinNode {

        @Children protected final RNode[] arguments;

        private final RBuiltinFactory builtin;

        public RCustomBuiltinNode(RBuiltinNode prev) {
            this(prev.getArguments(), prev.getBuiltin());
        }

        public RCustomBuiltinNode(RNode[] arguments, RBuiltinFactory builtin) {
            this.arguments = arguments;
            this.builtin = builtin;
        }

        @Override
        public RNode[] getArguments() {
            return arguments;
        }

        @Override
        public RBuiltinFactory getBuiltin() {
            return builtin;
        }

        @Override
        public String getSourceCode() {
            return "<custom builtin>";
        }

    }

}
