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
package com.oracle.truffle.r.nodes.builtin.base;

import static com.oracle.truffle.r.runtime.RBuiltinKind.PRIMITIVE;
import static com.oracle.truffle.r.runtime.RDispatch.INTERNAL_GENERIC;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.r.nodes.builtin.RInvisibleBuiltinNode;
import com.oracle.truffle.r.runtime.RBuiltin;
import com.oracle.truffle.r.runtime.RError;
import com.oracle.truffle.r.runtime.data.model.RAbstractContainer;
import com.oracle.truffle.r.runtime.data.model.RAbstractIntVector;

@RBuiltin(name = "length<-", kind = PRIMITIVE, parameterNames = {"x", ""}, dispatch = INTERNAL_GENERIC)
// 2nd parameter is "value", but should not be matched against, so ""
public abstract class UpdateLength extends RInvisibleBuiltinNode {

    @Override
    protected void createCasts(CastBuilder casts) {
        casts.toInteger(1, true, false, false);
    }

    @Specialization(guards = "isLengthOne(lengthVector)")
    protected RAbstractContainer updateLength(RAbstractContainer container, RAbstractIntVector lengthVector) {
        controlVisibility();
        return container.resize(lengthVector.getDataAt(0));
    }

    @SuppressWarnings("unused")
    @Specialization
    protected Object updateLengthError(Object vector, Object lengthVector) {
        controlVisibility();
        CompilerDirectives.transferToInterpreter();
        throw RError.error(this, RError.Message.INVALID_UNNAMED_VALUE);
    }

    protected static boolean isLengthOne(RAbstractIntVector length) {
        return length.getLength() == 1;
    }
}
