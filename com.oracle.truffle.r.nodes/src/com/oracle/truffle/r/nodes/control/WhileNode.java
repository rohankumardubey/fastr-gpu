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
package com.oracle.truffle.r.nodes.control;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.LoopNode;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.SourceSection;
import com.oracle.truffle.api.utilities.BranchProfile;
import com.oracle.truffle.api.utilities.ConditionProfile;
import com.oracle.truffle.r.nodes.RASTUtils;
import com.oracle.truffle.r.nodes.RRootNode;
import com.oracle.truffle.r.nodes.unary.ConvertBooleanNode;
import com.oracle.truffle.r.runtime.RDeparse;
import com.oracle.truffle.r.runtime.RInternalError;
import com.oracle.truffle.r.runtime.RRuntime;
import com.oracle.truffle.r.runtime.RSerialize;
import com.oracle.truffle.r.runtime.VisibilityController;
import com.oracle.truffle.r.runtime.data.RDataFactory;
import com.oracle.truffle.r.runtime.data.RNull;
import com.oracle.truffle.r.runtime.env.REnvironment;
import com.oracle.truffle.r.runtime.gnur.SEXPTYPE;
import com.oracle.truffle.r.runtime.nodes.RBaseNode;
import com.oracle.truffle.r.runtime.nodes.RNode;
import com.oracle.truffle.r.runtime.nodes.RSyntaxNode;

public final class WhileNode extends AbstractLoopNode implements RSyntaxNode, VisibilityController {

    @Child private LoopNode loop;

    /**
     * Also used for {@code repeat}, with a {@code TRUE} condition.
     */
    private final boolean isRepeat;

    private WhileNode(RSyntaxNode condition, RSyntaxNode body, boolean isRepeat) {
        this.loop = Truffle.getRuntime().createLoopNode(new WhileRepeatingNode(this, ConvertBooleanNode.create(condition), body.asRNode()));
        this.isRepeat = isRepeat;
    }

    public static WhileNode create(RSyntaxNode condition, RSyntaxNode body, boolean isRepeat) {
        return new WhileNode(condition, body, isRepeat);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        loop.executeLoop(frame);
        forceVisibility(false);
        return RNull.instance;
    }

    public ConvertBooleanNode getCondition() {
        return getRepeatingNode().getCondition();
    }

    public RNode getBody() {
        return getRepeatingNode().getBody();
    }

    private WhileRepeatingNode getRepeatingNode() {
        return (WhileRepeatingNode) loop.getRepeatingNode();
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    @Override
    public void deparseImpl(RDeparse.State state) {
        state.startNodeDeparse(this);
        if (isRepeat) {
            state.append("repeat ");
        } else {
            state.append("while (");
            getCondition().deparse(state);
            state.append(") ");
        }
        state.writeOpenCurlyNLIncIndent();
        getBody().deparse(state);
        state.decIndentWriteCloseCurly();
        state.endNodeDeparse(this);
    }

    @Override
    public void serializeImpl(RSerialize.State state) {
        state.setAsBuiltin(isRepeat ? "repeat" : "while");
        if (!isRepeat) {
            state.openPairList(SEXPTYPE.LISTSXP);
            // condition
            state.serializeNodeSetCar(getCondition());
        }
        state.openPairList(SEXPTYPE.LISTSXP);
        state.openBrace();
        state.serializeNodeSetCdr(getBody(), SEXPTYPE.LISTSXP);
        state.closeBrace();
        state.linkPairList(isRepeat ? 1 : 2);
        state.setCdr(state.closePairList());
    }

    @Override
    public RSyntaxNode substituteImpl(REnvironment env) {
        return create(getCondition().substitute(env), getBody().substitute(env), isRepeat);
    }

    public int getRlengthImpl() {
        return isRepeat ? 2 : 3;
    }

    @Override
    public Object getRelementImpl(int indexArg) {
        int index = indexArg;
        if (isRepeat && index == 1) {
            index = 2;
        }
        switch (index) {
            case 0:
                return RDataFactory.createSymbol(isRepeat ? "repeat" : "while");
            case 1:
                return RASTUtils.createLanguageElement(getCondition());
            case 2:
                return RASTUtils.createLanguageElement(getBody());
            default:
                throw RInternalError.shouldNotReachHere();
        }
    }

    @Override
    public boolean getRequalsImpl(RSyntaxNode other) {
        throw RInternalError.unimplemented();
    }

    private static final class WhileRepeatingNode extends RBaseNode implements RepeatingNode {

        @Child private ConvertBooleanNode condition;
        @Child private RNode body;

        private final ConditionProfile conditionProfile = ConditionProfile.createCountingProfile();
        private final BranchProfile breakBlock = BranchProfile.create();
        private final BranchProfile nextBlock = BranchProfile.create();

        // used as RSyntaxNode
        private final WhileNode whileNode;

        public WhileRepeatingNode(WhileNode whileNode, ConvertBooleanNode condition, RNode body) {
            this.whileNode = whileNode;
            this.condition = condition;
            this.body = body;
            // pre-initialize the profile so that loop exits to not deoptimize
            conditionProfile.profile(false);
        }

        public RNode getBody() {
            return body;
        }

        public ConvertBooleanNode getCondition() {
            return condition;
        }

        public boolean executeRepeating(VirtualFrame frame) {
            try {
                if (conditionProfile.profile(condition.executeByte(frame) == RRuntime.LOGICAL_TRUE)) {
                    body.execute(frame);
                    return true;
                } else {
                    return false;
                }
            } catch (BreakException e) {
                breakBlock.enter();
                return false;
            } catch (NextException e) {
                nextBlock.enter();
                return true;
            }
        }

        @Override
        protected RSyntaxNode getRSyntaxNode() {
            return whileNode;
        }

        @Override
        public String toString() {
            RootNode rootNode = getRootNode();
            String function = "?";
            if (rootNode instanceof RRootNode) {
                function = rootNode.toString();
            }
            SourceSection sourceSection = getRSyntaxNode().getSourceSection();
            int startLine = -1;
            String shortDescription = "?";
            if (sourceSection != null) {
                startLine = sourceSection.getStartLine();
                shortDescription = sourceSection.getSource() == null ? shortDescription : sourceSection.getSource().getShortName();
            }
            return String.format("while loop at %s<%s:%d>", function, shortDescription, startLine);
        }
    }
}
