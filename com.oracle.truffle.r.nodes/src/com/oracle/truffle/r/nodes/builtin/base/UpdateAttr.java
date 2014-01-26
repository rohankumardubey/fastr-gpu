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
package com.oracle.truffle.r.nodes.builtin.base;

import java.util.*;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.nodes.unary.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.model.*;

@RBuiltin("attr<-")
@SuppressWarnings("unused")
public abstract class UpdateAttr extends RBuiltinNode {

    @Child UpdateNames updateNames;
    @Child UpdateDimNames updateDimNames;
    @Child private CastIntegerNode castInteger;
    @Child private CastToVectorNode castVector;
    @Child private CastListNode castList;

    private RAbstractVector updateNames(VirtualFrame frame, RAbstractVector vector, Object o) {
        if (updateNames == null) {
            CompilerDirectives.transferToInterpreter();
            updateNames = adoptChild(UpdateNamesFactory.create(new RNode[1], getContext(), getBuiltin()));
        }
        return (RAbstractVector) updateNames.executeStringVector(frame, vector, o);
    }

    private RAbstractVector updateDimNames(VirtualFrame frame, RAbstractVector vector, Object o) {
        if (updateDimNames == null) {
            CompilerDirectives.transferToInterpreter();
            updateDimNames = adoptChild(UpdateDimNamesFactory.create(new RNode[1], getContext(), getBuiltin()));
        }
        return (RAbstractVector) updateDimNames.executeList(frame, vector, o);
    }

    private RAbstractIntVector castInteger(VirtualFrame frame, RAbstractVector vector) {
        if (castInteger == null) {
            CompilerDirectives.transferToInterpreter();
            castInteger = adoptChild(CastIntegerNodeFactory.create(null, true, false, getContext()));
        }
        return (RAbstractIntVector) castInteger.executeCast(frame, vector);
    }

    private RAbstractVector castVector(VirtualFrame frame, Object value) {
        if (castVector == null) {
            CompilerDirectives.transferToInterpreter();
            castVector = adoptChild(CastToVectorNodeFactory.create(null));
        }
        return castVector.executeRAbstractVector(frame, value);
    }

    private RList castList(VirtualFrame frame, Object value) {
        if (castList == null) {
            CompilerDirectives.transferToInterpreter();
            castList = adoptChild(CastListNodeFactory.create(null));
        }
        return castList.executeList(frame, value);
    }

    @Specialization(guards = "nullValue")
    public RAbstractVector updateAttr(VirtualFrame frame, RAbstractVector vector, String name, RNull value) {
        RVector resultVector = vector.materialize();
        if (resultVector.isShared()) {
            resultVector = resultVector.copy();
        }
        if (name.equals(RRuntime.DIM_ATTR_KEY)) {
            resultVector.setDimensions(null, getSourceSection());
        } else if (name.equals(RRuntime.NAMES_ATTR_KEY)) {
            return updateNames(frame, resultVector, value);
        } else if (name.equals(RRuntime.DIMNAMES_ATTR_KEY)) {
            return updateDimNames(frame, resultVector, value);
        } else if (resultVector.getAttributes() != null) {
            resultVector.getAttributes().remove(name);
        }
        return resultVector;
    }

    @Specialization(guards = "!nullValue")
    public RAbstractVector updateAttr(VirtualFrame frame, RAbstractVector vector, String name, Object value) {
        RVector resultVector = vector.materialize();
        if (resultVector.isShared()) {
            resultVector = resultVector.copy();
        }
        if (name.equals(RRuntime.DIM_ATTR_KEY)) {
            RAbstractIntVector dimsVector = castInteger(frame, castVector(frame, value));
            if (dimsVector.getLength() == 0) {
                throw RError.getLengthZeroDimInvalid(getSourceSection());
            }
            resultVector.setDimensions(dimsVector.materialize().getDataCopy(), getSourceSection());
        } else if (name.equals(RRuntime.NAMES_ATTR_KEY)) {
            return updateNames(frame, resultVector, value);
        } else if (name.equals(RRuntime.DIMNAMES_ATTR_KEY)) {
            return updateDimNames(frame, resultVector, value);
        } else {
            if (resultVector.getAttributes() == null) {
                resultVector.setAttributes(new LinkedHashMap<String, Object>());
            }
            resultVector.getAttributes().put(name, value);
        }
        return resultVector;
    }

    @Specialization(guards = "!nullValue")
    public RAbstractVector updateAttr(VirtualFrame frame, RAbstractVector vector, RStringVector name, Object value) {
        return updateAttr(frame, vector, name.getDataAt(0), value);
    }

    // the guard is necessary as RNull and Object cannot be distinguished in case of multiple
    // specializations, such as in: x<-1; attr(x, "dim")<-1; attr(x, "dim")<-NULL
    public boolean nullValue(RAbstractVector vector, Object name, Object value) {
        return value == RNull.instance;
    }
}
