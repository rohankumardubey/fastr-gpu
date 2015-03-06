/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

import static com.oracle.truffle.r.runtime.RBuiltinKind.*;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.nodes.unary.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.model.*;

@SuppressWarnings("unused")
@RBuiltin(name = "ngettext", kind = INTERNAL, parameterNames = {"n", "msg1", "msg2", "domain"})
public abstract class NGetText extends RBuiltinNode {

    @Override
    public RNode[] getParameterValues() {
        // n, msg1, msg2, domain = NULL
        return new RNode[]{ConstantNode.create(RMissing.instance), ConstantNode.create(RMissing.instance), ConstantNode.create(RMissing.instance), ConstantNode.create(RNull.instance)};
    }

    @CreateCast("arguments")
    public RNode[] createCastValue(RNode[] children) {
        return new RNode[]{CastIntegerNodeGen.create(children[0], false, false, false), children[1], children[2], children[3]};
    }

    @Specialization(guards = "wrongNVector(nVector)")
    protected String getTextEmpty(RAbstractIntVector nVector, String msg1, String msg2, Object domain) {
        throw RError.error(getEncapsulatingSourceSection(), RError.Message.INVALID_ARGUMENT, "n");
    }

    @Specialization(guards = "!wrongNVector(nVector)")
    protected String getText(RAbstractIntVector nVector, String msg1, String msg2, Object domain) {
        int n = nVector.getDataAt(0);
        return n == 1 ? msg1 : msg2;
    }

    @Specialization(guards = "!wrongNVector(nVector)")
    protected String getTextMsg1Null(RAbstractIntVector nVector, RNull msg1, RNull msg2, Object domain) {
        throw RError.error(getEncapsulatingSourceSection(), RError.Message.MUST_BE_STRING, "msg1");
    }

    @Specialization(guards = "!wrongNVector(nVector)")
    protected String getTextMsg1Null(RAbstractIntVector nVector, RNull msg1, RAbstractVector msg2, Object domain) {
        throw RError.error(getEncapsulatingSourceSection(), RError.Message.MUST_BE_STRING, "msg1");
    }

    @Specialization(guards = {"!wrongNVector(nVector)", "!msgStringVectorOneElem(msg1)"})
    protected String getTextMsg1WrongMsg2Null(RAbstractIntVector nVector, RAbstractVector msg1, RNull msg2, Object domain) {
        throw RError.error(getEncapsulatingSourceSection(), RError.Message.MUST_BE_STRING, "msg1");
    }

    @Specialization(guards = {"!wrongNVector(nVector)", "!msgStringVectorOneElem(msg1)"})
    protected String getTextMsg1Wrong(RAbstractIntVector nVector, RAbstractVector msg1, RAbstractVector msg2, Object domain) {
        throw RError.error(getEncapsulatingSourceSection(), RError.Message.MUST_BE_STRING, "msg1");
    }

    @Specialization(guards = {"!wrongNVector(nVector)", "msgStringVectorOneElem(msg1)"})
    protected String getTextMsg1(RAbstractIntVector nVector, RAbstractVector msg1, RNull msg2, Object domain) {
        throw RError.error(getEncapsulatingSourceSection(), RError.Message.MUST_BE_STRING, "msg2");
    }

    @Specialization(guards = {"!wrongNVector(nVector)", "msgStringVectorOneElem(msg1)", "!msgStringVectorOneElem(msg2)"})
    protected String getTextMsg2Wrong(RAbstractIntVector nVector, RAbstractVector msg1, RAbstractVector msg2, Object domain) {
        throw RError.error(getEncapsulatingSourceSection(), RError.Message.MUST_BE_STRING, "msg2");
    }

    @Specialization(guards = {"!wrongNVector(nVector)", "msgStringVectorOneElem(msg1)", "msgStringVectorOneElem(msg2)"})
    protected String getTextMsg1(RAbstractIntVector nVector, RAbstractVector msg1, RAbstractVector msg2, Object domain) {
        return getText(nVector, ((RAbstractStringVector) msg1).getDataAt(0), ((RAbstractStringVector) msg2).getDataAt(0), domain);
    }

    protected boolean wrongNVector(RAbstractIntVector nVector) {
        return nVector.getLength() == 0 || (nVector.getLength() > 0 && nVector.getDataAt(0) < 0);
    }

    protected boolean msgStringVectorOneElem(RAbstractVector msg1) {
        return msg1.getElementClass() == RString.class && msg1.getLength() == 1;
    }

}
