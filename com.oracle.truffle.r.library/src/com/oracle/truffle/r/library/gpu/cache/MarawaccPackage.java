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
package com.oracle.truffle.r.library.gpu.cache;

import uk.ac.ed.datastructures.common.PArray;
import uk.ac.ed.jpai.ArrayFunction;

import com.oracle.truffle.r.library.gpu.types.TypeInfo;

public class MarawaccPackage {

    private ArrayFunction<?, ?> arrayFunction;
    private TypeInfo type;
    @SuppressWarnings("rawtypes") private PArray pArray;
    private Object output;

    public MarawaccPackage(ArrayFunction<?, ?> function) {
        this.arrayFunction = function;
    }

    public void setTypeInfo(TypeInfo t) {
        this.type = t;
    }

    public TypeInfo getTypeInfo() {
        return this.type;
    }

    @SuppressWarnings("rawtypes")
    public PArray getpArray() {
        return pArray;
    }

    @SuppressWarnings("rawtypes")
    public void setpArray(PArray pArray) {
        this.pArray = pArray;
    }

    public ArrayFunction<?, ?> getArrayFunction() {
        return this.arrayFunction;
    }

    public void setOutput(Object value) {
        this.output = value;
    }

    public Object getExecutionValue() {
        return this.output;
    }
}
