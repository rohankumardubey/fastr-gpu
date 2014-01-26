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

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.Node.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;

@SuppressWarnings("unused")
@RBuiltin("is.na")
public abstract class IsNA extends RBuiltinNode {

    @Child IsNA recursiveIsNA;

    private Object isNARecursive(VirtualFrame frame, Object o) {
        if (recursiveIsNA == null) {
            CompilerDirectives.transferToInterpreter();
            recursiveIsNA = adoptChild(IsNAFactory.create(new RNode[1], getContext(), getBuiltin()));
        }
        return recursiveIsNA.execute(frame, o);
    }

    public abstract Object execute(VirtualFrame frame, Object o);

    @Specialization
    public byte isNA(int value) {
        return RRuntime.asLogical(RRuntime.isNA(value));
    }

    @Specialization
    public RLogicalVector isNA(RIntVector vector) {
        byte[] resultVector = new byte[vector.getLength()];
        for (int i = 0; i < vector.getLength(); i++) {
            resultVector[i] = (RRuntime.isNA(vector.getDataAt(i)) ? RRuntime.LOGICAL_TRUE : RRuntime.LOGICAL_FALSE);
        }
        return RDataFactory.createLogicalVector(resultVector, RDataFactory.COMPLETE_VECTOR, vector.getDimensions(), vector.getNames());
    }

    @Specialization
    public byte isNA(double value) {
        return RRuntime.asLogical(RRuntime.isNA(value));
    }

    @Specialization
    public RLogicalVector isNA(RDoubleVector vector) {
        byte[] resultVector = new byte[vector.getLength()];
        for (int i = 0; i < vector.getLength(); i++) {
            resultVector[i] = (RRuntime.isNA(vector.getDataAt(i)) ? RRuntime.LOGICAL_TRUE : RRuntime.LOGICAL_FALSE);
        }
        return RDataFactory.createLogicalVector(resultVector, RDataFactory.COMPLETE_VECTOR, vector.getDimensions(), vector.getNames());
    }

    @Specialization
    public byte isNA(String value) {
        // FIXME NA for Strings
        return RRuntime.LOGICAL_FALSE;
    }

    @Specialization
    public byte isNA(RStringVector value) {
        return RRuntime.LOGICAL_FALSE;
    }

    @Specialization
    public RLogicalVector isNA(VirtualFrame frame, RList list) {
        byte[] resultVector = new byte[list.getLength()];
        for (int i = 0; i < list.getLength(); i++) {
            Object result = isNARecursive(frame, list.getDataAt(i));
            byte isNAResult;
            if (result instanceof Byte) {
                isNAResult = (Byte) result;
            } else if (result instanceof RLogicalVector) {
                RLogicalVector vector = (RLogicalVector) result;
                // result is false unless that element is a length-one atomic vector
                // and the single element of that vector is regarded as NA
                isNAResult = (vector.getLength() == 1) ? vector.getDataAt(0) : RRuntime.LOGICAL_FALSE;
            } else {
                throw new UnsupportedOperationException("unhandled return type in isNA(list)");
            }
            resultVector[i] = isNAResult;
        }
        return RDataFactory.createLogicalVector(resultVector, RDataFactory.COMPLETE_VECTOR);
    }

    @Specialization
    public byte isNA(byte value) {
        return RRuntime.asLogical(RRuntime.isNA(value));
    }

    @Specialization
    public RLogicalVector isNA(RLogicalVector vector) {
        byte[] resultVector = new byte[vector.getLength()];
        for (int i = 0; i < vector.getLength(); i++) {
            resultVector[i] = (RRuntime.isNA(vector.getDataAt(i)) ? RRuntime.LOGICAL_TRUE : RRuntime.LOGICAL_FALSE);
        }
        return RDataFactory.createLogicalVector(resultVector, RDataFactory.COMPLETE_VECTOR, vector.getDimensions(), vector.getNames());
    }

    @Specialization
    public byte isNA(RNull value) {
        return RRuntime.LOGICAL_FALSE;
    }
}
