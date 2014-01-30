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
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.nodes.unary.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.model.*;
import com.oracle.truffle.r.runtime.ops.na.*;

@RBuiltin({"as.double", "as.numeric"})
@SuppressWarnings("unused")
public abstract class AsDouble extends RBuiltinNode {

    @Child CastDoubleNode castDoubleNode;

    private void initCast() {
        if (castDoubleNode == null) {
            CompilerDirectives.transferToInterpreter();
            castDoubleNode = adoptChild(CastDoubleNodeFactory.create(null, false, false));
        }
    }

    private double castDouble(VirtualFrame frame, int o) {
        initCast();
        return (double) castDoubleNode.executeDouble(frame, o);
    }

    private double castDouble(VirtualFrame frame, double o) {
        initCast();
        return (double) castDoubleNode.executeDouble(frame, o);
    }

    private double castDouble(VirtualFrame frame, byte o) {
        initCast();
        return (double) castDoubleNode.executeDouble(frame, o);
    }

    private double castDouble(VirtualFrame frame, Object o) {
        initCast();
        return (double) castDoubleNode.executeDouble(frame, o);
    }

    private RDoubleVector castDoubleVector(VirtualFrame frame, Object o) {
        initCast();
        return (RDoubleVector) castDoubleNode.executeDoubleVector(frame, o);
    }

    @Specialization
    public double asDouble(double value) {
        return value;
    }

    @Specialization(order = 10)
    public double asDoubleInt(VirtualFrame frame, int value) {
        return castDouble(frame, value);
    }

    @Specialization
    public double asDouble(VirtualFrame frame, byte value) {
        return castDouble(frame, value);
    }

    @Specialization
    public double asDouble(VirtualFrame frame, RComplex value) {
        return castDouble(frame, value);
    }

    @Specialization
    public double asDouble(VirtualFrame frame, String value) {
        return castDouble(frame, value);
    }

    @Specialization
    public RDoubleVector asDouble(RNull vector) {
        return RDataFactory.createDoubleVector(0);
    }

    @Specialization
    public RDoubleVector asDouble(RDoubleVector vector) {
        return RDataFactory.createDoubleVector(vector.getDataCopy(), vector.isComplete());
    }

    @Specialization
    public RDoubleVector asDouble(RDoubleSequence sequence) {
        return (RDoubleVector) sequence.createVector();
    }

    @Specialization
    public RDoubleVector asDouble(VirtualFrame frame, RIntSequence sequence) {
        double current = sequence.getStart();
        double[] result = new double[sequence.getLength()];
        for (int i = 0; i < sequence.getLength(); ++i) {
            result[i] = castDouble(frame, current);
            current += sequence.getStride();
        }
        return RDataFactory.createDoubleVector(result, RDataFactory.INCOMPLETE_VECTOR);
    }

    @Specialization
    public RDoubleVector asDouble(VirtualFrame frame, RAbstractVector vector) {
        return castDoubleVector(frame, vector);
    }
}
