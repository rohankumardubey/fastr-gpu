/*
 * Copyright (c) 2015, 2015, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.library.fastr;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.r.nodes.builtin.RExternalBuiltinNode;
import com.oracle.truffle.r.runtime.data.RNull;
import com.oracle.truffle.r.runtime.data.model.RAbstractStringVector;
import com.oracle.truffle.r.runtime.instrument.RPackageSource;

public class FastRPkgSource {

    public abstract static class PreLoad extends RExternalBuiltinNode.Arg2 {
        @Specialization
        protected RNull preLoad(RAbstractStringVector pkg, RAbstractStringVector fname) {
            RPackageSource.preLoad(pkg.getDataAt(0), fname.getDataAt(0));
            return RNull.instance;
        }
    }

    public abstract static class PostLoad extends RExternalBuiltinNode.Arg3 {
        @Specialization
        protected RNull postLoad(RAbstractStringVector pkg, RAbstractStringVector fname, Object val) {
            RPackageSource.postLoad(pkg.getDataAt(0), fname.getDataAt(0), val);
            return RNull.instance;
        }
    }

    public abstract static class Done extends RExternalBuiltinNode.Arg1 {
        @Specialization
        protected RNull done(@SuppressWarnings("unused") RAbstractStringVector pkg) {
            RPackageSource.saveMap();
            return RNull.instance;
        }
    }

}
