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

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.r.nodes.RNode;
import com.oracle.truffle.r.nodes.access.ConstantNode;
import com.oracle.truffle.r.nodes.builtin.RBuiltinPackages;
import com.oracle.truffle.r.nodes.builtin.RInvisibleBuiltinNode;
import com.oracle.truffle.r.runtime.RArguments;
import com.oracle.truffle.r.runtime.RBuiltin;
import com.oracle.truffle.r.runtime.RError;
import com.oracle.truffle.r.runtime.RRuntime;
import com.oracle.truffle.r.runtime.data.RMissing;
import com.oracle.truffle.r.runtime.data.RNull;
import com.oracle.truffle.r.runtime.data.model.*;
import com.oracle.truffle.r.runtime.env.REnvironment;
import com.oracle.truffle.r.runtime.env.REnvironment.PutException;

import static com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import static com.oracle.truffle.r.runtime.RBuiltinKind.INTERNAL;

@RBuiltin(name = "remove", kind = INTERNAL, parameterNames = {"list", "envir", "inherits"})
public abstract class Rm extends RInvisibleBuiltinNode {

    public static Rm create(String name) {
        RNode[] args = getParameterValues0();
        args[0] = ConstantNode.create(name);
        return RmFactory.create(args, RBuiltinPackages.lookupBuiltin("remove"), null);
    }

    private static RNode[] getParameterValues0() {
        return new RNode[]{ConstantNode.create(RMissing.instance), ConstantNode.create(RMissing.instance), ConstantNode.create(RRuntime.LOGICAL_FALSE)};
    }

    @Override
    public RNode[] getParameterValues() {
        return getParameterValues0();
    }

    // this specialization is for internal use only
    @Specialization
    @SuppressWarnings("unused")
    protected Object rm(VirtualFrame frame, String name, RMissing envir, byte inherits) {
        controlVisibility();
        removeFromFrame(frame, name);
        return RNull.instance;
    }

    @Specialization
    @TruffleBoundary
    @SuppressWarnings("unused")
    protected Object rm(RAbstractStringVector list, REnvironment envir, byte inherits) {
        controlVisibility();
        try {
            for (int i = 0; i < list.getLength(); i++) {
                if (envir == REnvironment.globalEnv()) {
                    removeFromFrame(envir.getFrame(), list.getDataAt(i));
                } else {
                    envir.rm(list.getDataAt(i));
                }
            }
        } catch (PutException ex) {
            throw RError.error(getEncapsulatingSourceSection(), ex);
        }
        return RNull.instance;
    }

    private void removeFromFrame(Frame frame, String x) {
        // standard case for lookup in current frame
        Frame frm = frame;
        FrameSlot fs = frame.getFrameDescriptor().findFrameSlot(x);
        while (fs == null && frm != null) {
            frm = RArguments.getEnclosingFrame(frm);
            if (frm != null) {
                fs = frm.getFrameDescriptor().findFrameSlot(x);
            }
        }
        if (fs == null) {
            RError.warning(this.getEncapsulatingSourceSection(), RError.Message.UNKNOWN_OBJECT, x);
        } else {
            frm.setObject(fs, null); // use null (not an R value) to represent "undefined"
        }
    }

}
