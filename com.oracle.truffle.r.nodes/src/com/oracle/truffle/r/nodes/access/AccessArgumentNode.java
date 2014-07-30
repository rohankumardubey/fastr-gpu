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
package com.oracle.truffle.r.nodes.access;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.function.PromiseNode.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.RPromise.*;

/**
 * Simple {@link RNode} that returns a function argument specified by its formal index. Used to
 * populate a function's environment.
 */
public class AccessArgumentNode extends RNode {

    private final int index;
    @SuppressWarnings("unused") private final AccessArgumentContext accessArgCtx;

    public AccessArgumentNode(int index, AccessArgumentContext accessArgCtx) {
        this.index = index;
        this.accessArgCtx = accessArgCtx;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        Object obj = RArguments.getArgument(frame, index);
        if (obj instanceof RPromise) {
            obj = handlePromise(frame, obj);
        } else if (obj instanceof Object[]) {
            Object[] varArgs = (Object[]) obj;
            for (int i = 0; i < varArgs.length; i++) {
                varArgs[i] = handlePromise(frame, varArgs[i]);
            }
        }
        return obj;
    }

    @SuppressWarnings("static-method")
    private Object handlePromise(VirtualFrame frame, Object promiseObj) {
        RPromise promise = (RPromise) promiseObj;
        assert promise.getEvalPolicy() != EvalPolicy.RAW : "EvalPolicy == RAW in AAN!?!?";
        assert promise.getType() != PromiseType.NO_ARG;

        if (promise.getType() == PromiseType.ARG_DEFAULT && promise.getEvalPolicy() == EvalPolicy.PROMISED && promise.getEnv() == null) {
            // In this case the promise might lack the proper REnvironment, as it was created before
            // the environment was
            promise.updateEnv(REnvironment.frameToEnvironment(frame));
        }

        // Now force evaluation for STRICT
        if (promise.getEvalPolicy() == EvalPolicy.STRICT) {
            return promise.evaluate(frame);
        }
        return promiseObj;
    }

    public static class AccessArgumentContext implements IEnvironmentProvider {
        private REnvironment calleeEnv = null;

        public REnvironment getREnvironmentFor(VirtualFrame frame) {
            if (calleeEnv == null || calleeEnv.getFrame() != frame) {
                calleeEnv = REnvironment.frameToEnvironment(frame);
            }
            return calleeEnv;
        }
    }
}
