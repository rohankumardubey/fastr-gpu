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

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import com.oracle.truffle.api.utilities.*;
import com.oracle.truffle.r.nodes.*;

@TypeSystemReference(RTypes.class)
public abstract class FrameSlotNode extends Node {

    public static enum InternalFrameSlot {
        /**
         * Stores the expression that needs to be executed when the function associated with the
         * frame terminates.
         */
        OnExit
    }

    public abstract boolean hasValue(Frame frame);

    public abstract boolean isWrongDescriptor(FrameDescriptor otherDescriptor);

    public FrameSlot executeFrameSlot(@SuppressWarnings("unused") VirtualFrame frame) {
        throw new UnsupportedOperationException();
    }

    static FrameSlot findFrameSlot(Frame frame, Object identifier) {
        return frame.getFrameDescriptor().findFrameSlot(identifier);
    }

    static Assumption getAssumption(Frame frame, Object identifier) {
        return frame.getFrameDescriptor().getNotInFrameAssumption(identifier);
    }

    public static FrameSlotNode create(String name) {
        return new UnresolvedFrameSlotNode(name, false);
    }

    public static FrameSlotNode create(InternalFrameSlot slot, boolean createIfAbsent) {
        return new UnresolvedFrameSlotNode(slot, createIfAbsent);
    }

    public static FrameSlotNode create(FrameSlot slot) {
        return new PresentFrameSlotNode(slot);
    }

    @NodeInfo(cost = NodeCost.UNINITIALIZED)
    private static final class UnresolvedFrameSlotNode extends FrameSlotNode {

        private final Object identifier;
        private final boolean createIfAbsent;

        public UnresolvedFrameSlotNode(Object identifier, boolean createIfAbsent) {
            this.identifier = identifier;
            this.createIfAbsent = createIfAbsent;
        }

        @Override
        public boolean hasValue(Frame frame) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return resolveFrameSlot(frame).hasValue(frame);
        }

        @Override
        public FrameSlot executeFrameSlot(VirtualFrame frame) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            return resolveFrameSlot(frame).executeFrameSlot(frame);
        }

        private FrameSlotNode resolveFrameSlot(Frame frame) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            FrameDescriptor frameDescriptor = frame.getFrameDescriptor();
            FrameSlotNode newNode;
            FrameSlot frameSlot;
            if (createIfAbsent) {
                frameSlot = frameDescriptor.findOrAddFrameSlot(identifier);
            } else {
                frameSlot = frameDescriptor.findFrameSlot(identifier);
            }
            if (frameSlot != null) {
                newNode = new PresentFrameSlotNode(frameSlot);
            } else {
                newNode = new AbsentFrameSlotNode(getAssumption(frame, identifier), frameDescriptor, identifier);
            }
            return replace(newNode);
        }

        @Override
        public boolean isWrongDescriptor(FrameDescriptor otherDescriptor) {
            // Not yet specialized, so none is wrong
            return false;
        }
    }

    private static final class AbsentFrameSlotNode extends FrameSlotNode {

        @CompilationFinal private Assumption assumption;
        private final FrameDescriptor frameDescriptor;  // The descriptor the assumption was made in
        private final Object identifier;

        public AbsentFrameSlotNode(Assumption assumption, FrameDescriptor frameDescriptor, Object identifier) {
            this.assumption = assumption;
            this.frameDescriptor = frameDescriptor;
            this.identifier = identifier;
        }

        @Override
        public boolean hasValue(Frame frame) {
            assert frameDescriptor == frame.getFrameDescriptor();

            try {
                assumption.check();
            } catch (InvalidAssumptionException e) {
                final FrameSlot frameSlot = frame.getFrameDescriptor().findFrameSlot(identifier);
                if (frameSlot != null) {
                    return replace(new PresentFrameSlotNode(frameSlot)).hasValue(frame);
                } else {
                    assumption = frame.getFrameDescriptor().getVersion();
                }
            }
            return false;
        }

        @Override
        public boolean isWrongDescriptor(FrameDescriptor otherDescriptor) {
            return frameDescriptor != otherDescriptor;
        }
    }

    private static final class PresentFrameSlotNode extends FrameSlotNode {

        private final ConditionProfile initializedProfile = ConditionProfile.createBinaryProfile();
        private final FrameSlot frameSlot;

        private final ValueProfile frameTypeProfile = ValueProfile.createClassProfile();

        public PresentFrameSlotNode(FrameSlot frameSlot) {
            this.frameSlot = frameSlot;
        }

        @Override
        public FrameSlot executeFrameSlot(VirtualFrame frame) {
            return frameSlot;
        }

        private boolean isInitialized(Frame frame) {
            assert frameSlot.getFrameDescriptor() == frame.getFrameDescriptor();

            try {
                Frame typedFrame = frameTypeProfile.profile(frame);
                return !typedFrame.isObject(frameSlot) || typedFrame.getObject(frameSlot) != null;
            } catch (FrameSlotTypeException e) {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean hasValue(Frame frame) {
            return initializedProfile.profile(isInitialized(frame));
        }

        @Override
        public boolean isWrongDescriptor(FrameDescriptor otherDescriptor) {
            return otherDescriptor != frameSlot.getFrameDescriptor();
        }
    }
}
