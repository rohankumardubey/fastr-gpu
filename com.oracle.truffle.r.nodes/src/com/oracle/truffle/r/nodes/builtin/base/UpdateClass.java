/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Copyright (c) 2014, Purdue University
 * Copyright (c) 2014, Oracle and/or its affiliates
 *
 * All rights reserved.
 */

package com.oracle.truffle.r.nodes.builtin.base;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.binary.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.nodes.unary.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.model.*;

@RBuiltin(value = "class<-")
public abstract class UpdateClass extends RInvisibleBuiltinNode {

    @Child private CastTypeNode castTypeNode;
    @Child private CastStringNode castStringNode;
    @Child private Typeof typeof;

    public abstract Object execute(VirtualFrame frame, RAbstractContainer vector, Object o);

    @Specialization
    public Object setClass(VirtualFrame frame, RAbstractContainer arg, RAbstractVector className) {
        controlVisibility();
        if (className.getLength() == 0) {
            return setClass(arg, RNull.instance);
        }
        if (castStringNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            castStringNode = insert(CastStringNodeFactory.create(null, false, false, false, false));
        }
        Object result = castStringNode.executeCast(frame, className);
        return setClass(arg, (RStringVector) result);
    }

    @Specialization
    public Object setClass(RAbstractContainer arg, RStringVector className) {
        controlVisibility();
        RVector resultVector = arg.materializeNonSharedVector();
        return RVector.setClassAttr(resultVector, className, arg.getElementClass() == RVector.class ? arg : null);
    }

    @Specialization
    public Object setClass(RAbstractContainer arg, @SuppressWarnings("unused") RNull className) {
        controlVisibility();
        RVector resultVector = arg.materializeNonSharedVector();
        return RVector.setClassAttr(resultVector, null, arg.getElementClass() == RVector.class ? arg : null);
    }

    @Specialization
    public Object setClass(VirtualFrame frame, RAbstractVector arg, String className) {
        controlVisibility();
        initTypeof();
        if (!arg.isObject()) {
            final String argType = this.typeof.execute(frame, arg).getDataAt(0);
            if (argType.equals(className) || (className.equals(RRuntime.TYPE_NUMERIC) && (argType.equals(RRuntime.TYPE_INTEGER) || (argType.equals(RRuntime.TYPE_DOUBLE))))) {
                return arg;
            }
        }
        initCastTypeNode();
        Object result = castTypeNode.execute(frame, arg, className);
        if (result != null) {
            return setClass((RAbstractVector) result, RNull.instance);
        }
        if (className.equals(RRuntime.TYPE_MATRIX)) {
            if (arg.isMatrix()) {
                return setClass(arg, RNull.instance);
            }
            final int[] dimensions = arg.getDimensions();
            int dimLength = 0;
            if (dimensions != null) {
                dimLength = dimensions.length;
            }
            throw RError.getNotMatixUpdateClass(getEncapsulatingSourceSection(), dimLength);
        }
        if (className.equals(RRuntime.TYPE_ARRAY)) {
            if (arg.isArray()) {
                return setClass(arg, RNull.instance);
            }
            throw RError.getNotArrayUpdateClass(getEncapsulatingSourceSection());
        }

        RVector resultVector = arg.materializeNonSharedVector();
        return RVector.setClassAttr(resultVector, RDataFactory.createStringVector(className), arg.getElementClass() == RVector.class ? arg : null);
    }

    private void initCastTypeNode() {
        if (castTypeNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            castTypeNode = insert(CastTypeNodeFactory.create(new RNode[2], this.getBuiltin()));
        }
    }

    private void initTypeof() {
        if (typeof == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            typeof = insert(TypeofFactory.create(new RNode[1], this.getBuiltin()));
        }
    }
}
