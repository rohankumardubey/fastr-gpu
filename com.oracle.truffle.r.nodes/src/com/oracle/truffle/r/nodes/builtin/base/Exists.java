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
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;

@RBuiltin("exists")
public abstract class Exists extends RBuiltinNode {

    private static final Object[] PARAMETER_NAMES = new Object[]{"x", "where", "envir", "frame", "mode", "inherits"};

    @CompilationFinal protected String lastName;
    @CompilationFinal protected boolean lastLookup;

    @Override
    public Object[] getParameterNames() {
        return PARAMETER_NAMES;
    }

    @Override
    public RNode[] getParameterValues() {
        return new RNode[]{ConstantNode.create(RMissing.instance), ConstantNode.create(-1), ConstantNode.create(RMissing.instance), ConstantNode.create(RMissing.instance),
                        ConstantNode.create(RRuntime.TYPE_ANY), ConstantNode.create(RRuntime.LOGICAL_TRUE)};
    }

    @Specialization(order = 10, guards = "noEnv")
    @SuppressWarnings("unused")
    public byte existsString(VirtualFrame frm, String name, Object where, RMissing envir, Object frame, String mode, byte inherits) {
        return frm.getFrameDescriptor().findFrameSlot(name) != null || (inherits == RRuntime.LOGICAL_TRUE && findEnclosing(RArguments.get(frm).getEnclosingFrame(), name)) ? RRuntime.LOGICAL_TRUE
                        : RRuntime.asLogical(inherits == RRuntime.LOGICAL_TRUE && packageLookup(name));
    }

    @Specialization(order = 11)
    @SuppressWarnings("unused")
    public byte existsStringEnv(String name, REnvironment where, RMissing envir, Object frame, String mode, byte inherits) {
        if (inherits == RRuntime.LOGICAL_FALSE) {
            return RRuntime.asLogical(where.get(name) != null);
        }
        for (REnvironment e = where; e != null; e = e.getParent()) {
            if (e.get(name) != null) {
                return RRuntime.LOGICAL_TRUE;
            }
        }
        return RRuntime.LOGICAL_FALSE;
    }

    @Specialization(order = 12)
    public byte existsStringEnv(RStringVector name, REnvironment where, RMissing envir, Object frame, String mode, byte inherits) {
        return existsStringEnv(name.getDataAt(0), where, envir, frame, mode, inherits);
    }

    private static boolean findEnclosing(MaterializedFrame frame, Object name) {
        if (frame == null) {
            // TODO lookup builtin
            return false;
        }
        return frame.getFrameDescriptor().findFrameSlot(name) != null || findEnclosing(RArguments.get(frame).getEnclosingFrame(), name);
    }

    private boolean packageLookup(String name) {
        if (!name.equals(lastName)) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            lastName = name;
            lastLookup = getContext().getLookup().lookup(getContext(), name) != null;
        }
        // FIXME deal with changes in packages due to deleting symbols
        return lastLookup;
    }

    @SuppressWarnings("unused")
    protected static boolean noEnv(String name, Object where, RMissing envir, Object frame, String mode, byte inherits) {
        return !(where instanceof REnvironment);
    }
}
