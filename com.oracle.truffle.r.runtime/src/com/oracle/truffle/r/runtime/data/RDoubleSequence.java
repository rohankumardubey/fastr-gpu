/*
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.runtime.data;

import com.oracle.truffle.api.*;
import com.oracle.truffle.r.runtime.data.model.*;

public final class RDoubleSequence extends RSequence implements RAbstractDoubleVector {

    private final double start;
    private final double stride;

    RDoubleSequence(double start, double stride, int length) {
        super(length);
        assert length > 0;
        this.start = start;
        this.stride = stride;
    }

    public double getStart() {
        return start;
    }

    public double getStride() {
        return stride;
    }

    private RDoubleVector populateVectorData(double[] result) {
        double current = start;
        for (int i = 0; i < getLength(); ++i) {
            result[i] = current;
            current += stride;
        }
        return RDataFactory.createDoubleVector(result, RDataFactory.COMPLETE_VECTOR);
    }

    @Override
    protected RDoubleVector internalCreateVector() {
        return populateVectorData(new double[getLength()]);
    }

    @Override
    public String toString() {
        CompilerAsserts.neverPartOfCompilation();
        return internalCreateVector().toString();
    }

    @Override
    public double getDataAt(int index) {
        assert index >= 0 && index < getLength();
        return start + stride * index;
    }

    @Override
    public RDoubleVector materialize() {
        return this.internalCreateVector();
    }

    public Class<?> getElementClass() {
        return RDouble.class;
    }

    @Override
    public Object getDataAtAsObject(int index) {
        return getDataAt(index);
    }

    @Override
    public RStringVector getClassHierarchy() {
        return RDoubleVector.implicitClassHeader;
    }

    @Override
    public RDoubleVector copyResized(int size, boolean fillNA) {
        double[] data = new double[size];
        populateVectorData(data);
        RDoubleVector.resizeData(data, data, getLength(), fillNA);
        return RDataFactory.createDoubleVector(data, !(fillNA && size > getLength()));
    }

    @Override
    public RDoubleVector createEmptySameType(int newLength, boolean newIsComplete) {
        return RDataFactory.createDoubleVector(new double[newLength], newIsComplete);
    }
}
