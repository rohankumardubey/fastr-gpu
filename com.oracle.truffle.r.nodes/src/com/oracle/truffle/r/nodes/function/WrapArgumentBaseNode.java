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
package com.oracle.truffle.r.nodes.function;

import com.oracle.truffle.api.source.*;
import com.oracle.truffle.api.utilities.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.env.*;

/**
 * {@link WrapArgumentBaseNode} is a super class of wrappers handling function arguments. As such it
 * is not really a syntax node, but it is created during parsing and therefore forms part of the
 * syntactic backbone. There are some internal uses also that are not syntactic in nature. As a
 * backbone node, it does not store a {@link SourceSection} attribute but overrides
 * {@code getSourceSection} to retrieve the info from the operand.
 */
public abstract class WrapArgumentBaseNode extends RNode implements RSyntaxNode {

    @Child protected RNode operand;

    private final BranchProfile everSeenVector;
    private final BranchProfile everSeenDataFrame;
    private final BranchProfile everSeenFactor;
    private final BranchProfile everSeenLanguage;

    protected final BranchProfile shareable;
    protected final BranchProfile nonShareable;

    protected WrapArgumentBaseNode(RNode operand, boolean initProfiles) {
        this.operand = operand;
        if (initProfiles) {
            everSeenVector = BranchProfile.create();
            everSeenDataFrame = BranchProfile.create();
            everSeenFactor = BranchProfile.create();
            everSeenLanguage = BranchProfile.create();
            shareable = BranchProfile.create();
            nonShareable = BranchProfile.create();
        } else {
            everSeenVector = null;
            everSeenDataFrame = null;
            everSeenFactor = null;
            everSeenLanguage = null;
            shareable = null;
            nonShareable = null;
        }
    }

    protected RShareable getShareable(Object result) {
        if (result instanceof RVector) {
            everSeenVector.enter();
            return (RVector) result;
        } else if (result instanceof RDataFrame) {
            everSeenDataFrame.enter();
            return ((RDataFrame) result).getVector();
        } else if (result instanceof RFactor) {
            everSeenFactor.enter();
            return ((RFactor) result).getVector();
        } else if (result instanceof RLanguage) {
            everSeenLanguage.enter();
            return (RLanguage) result;
        } else {
            nonShareable.enter();
            return null;
        }
    }

    public RNode getOperand() {
        return operand;
    }

    @Override
    public boolean isBackbone() {
        return true;
    }

    @Override
    public SourceSection getSourceSection() {
        // operand may not be an RSyntaxNode
        return getOperand().getEncapsulatingSourceSection();
    }

    @Override
    public SourceSection getEncapsulatingSourceSection() {
        return getOperand().getEncapsulatingSourceSection();
    }

    @Override
    public void deparse(RDeparse.State state) {
        RSyntaxNode.cast(getOperand()).deparse(state);
    }

    @Override
    public void serialize(RSerialize.State state) {
        RSyntaxNode.cast(getOperand()).serialize(state);
    }

    protected abstract RSyntaxNode createSubstitute(RNode sub);

    @Override
    public RSyntaxNode substitute(REnvironment env) {
        RNode sub = RSyntaxNode.cast(getOperand()).substitute(env).asRNode();
        if (sub instanceof RASTUtils.DotsNode) {
            return (RASTUtils.DotsNode) sub;
        } else {
            return createSubstitute(sub);
        }
    }
}
