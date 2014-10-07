/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Copyright (c) 2014, Purdue University
 * Copyright (c) 2014, Oracle and/or its affiliates
 *
 * All rights reserved.
 */

package com.oracle.truffle.r.nodes.builtin.base;

import java.util.*;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.CompilerDirectives.SlowPath;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.utilities.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.binary.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.nodes.unary.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.model.*;

@RBuiltin(name = "anyDuplicated", kind = RBuiltinKind.INTERNAL, parameterNames = {"x", "imcomparables", "fromLast"})
public abstract class AnyDuplicated extends RBuiltinNode {

    private final ConditionProfile fromLastProfile = ConditionProfile.createBinaryProfile();

    @Child private CastTypeNode castTypeNode;
    @Child private TypeofNode typeof;

    @Override
    public RNode[] getParameterValues() {
        return new RNode[]{ConstantNode.create(RMissing.instance), ConstantNode.create(RMissing.instance), ConstantNode.create(RMissing.instance)};
    }

    @CreateCast("arguments")
    public RNode[] castArguments(RNode[] arguments) {
        arguments[2] = CastLogicalNodeFactory.create(arguments[2], true, false, false);
        return arguments;
    }

    @SuppressWarnings("unused")
    @Specialization(guards = {"!isIncomparable", "!empty"})
    protected int anyDuplicatedFalseIncomparablesFromStart(RAbstractVector x, byte incomparables, byte fromLast) {
        if (fromLastProfile.profile(fromLast == RRuntime.LOGICAL_TRUE)) {
            return getIndexFromLast(x);
        } else {
            return getIndexFromStart(x);
        }
    }

    @Specialization(guards = {"isIncomparable", "!empty"})
    protected int anyDuplicatedTrueIncomparablesFromStart(VirtualFrame frame, RAbstractVector x, byte incomparables, byte fromLast) {
        initChildren();
        RType xType = typeof.execute(frame, x);
        RAbstractVector vector = (RAbstractVector) (castTypeNode.execute(frame, incomparables, xType));
        if (fromLastProfile.profile(fromLast == RRuntime.LOGICAL_TRUE)) {
            return getIndexFromLast(x, vector);
        } else {
            return getIndexFromStart(x, vector);
        }
    }

    @Specialization(guards = {"!empty"})
    protected int anyDuplicatedFromStart(VirtualFrame frame, RAbstractVector x, RAbstractVector incomparables, byte fromLast) {
        initChildren();
        RType xType = typeof.execute(frame, x);
        if (fromLastProfile.profile(fromLast == RRuntime.LOGICAL_TRUE)) {
            return getIndexFromLast(x, (RAbstractVector) (castTypeNode.execute(frame, incomparables, xType)));
        } else {
            return getIndexFromStart(x, (RAbstractVector) (castTypeNode.execute(frame, incomparables, xType)));
        }
    }

    @SuppressWarnings("unused")
    @Specialization(guards = "empty")
    protected int anyDuplicatedEmpty(RAbstractVector x, RAbstractVector incomparables, byte fromLast) {
        return 0;
    }

    @SlowPath
    private static int getIndexFromStart(RAbstractVector x, RAbstractVector incomparables) {
        HashSet<Object> incompContents = new HashSet<>();
        HashSet<Object> vectorContents = new HashSet<>();
        for (int i = 0; i < incomparables.getLength(); i++) {
            incompContents.add(incomparables.getDataAtAsObject(i));
        }
        vectorContents.add(x.getDataAtAsObject(0));
        for (int i = 1; i < x.getLength(); i++) {
            if (!incompContents.contains(x.getDataAtAsObject(i))) {
                if (vectorContents.contains(x.getDataAtAsObject(i))) {
                    return i + 1;
                } else {
                    vectorContents.add(x.getDataAtAsObject(i));
                }
            }
        }
        return 0;
    }

    @SlowPath
    private static int getIndexFromStart(RAbstractVector x) {
        HashSet<Object> vectorContents = new HashSet<>();
        vectorContents.add(x.getDataAtAsObject(0));
        for (int i = 1; i < x.getLength(); i++) {
            if (vectorContents.contains(x.getDataAtAsObject(i))) {
                return i + 1;
            } else {
                vectorContents.add(x.getDataAtAsObject(i));
            }
        }
        return 0;
    }

    @SlowPath
    public static int getIndexFromLast(RAbstractVector x, RAbstractVector incomparables) {
        HashSet<Object> incompContents = new HashSet<>();
        HashSet<Object> vectorContents = new HashSet<>();
        for (int i = 0; i < incomparables.getLength(); i++) {
            incompContents.add(incomparables.getDataAtAsObject(i));
        }
        vectorContents.add(x.getDataAtAsObject(x.getLength() - 1));
        for (int i = x.getLength() - 2; i >= 0; i--) {
            if (!incompContents.contains(x.getDataAtAsObject(i))) {
                if (vectorContents.contains(x.getDataAtAsObject(i))) {
                    return i + 1;
                } else {
                    vectorContents.add(x.getDataAtAsObject(i));
                }
            }
        }
        return 0;
    }

    @SlowPath
    private static int getIndexFromLast(RAbstractVector x) {
        HashSet<Object> vectorContents = new HashSet<>();
        vectorContents.add(x.getDataAtAsObject(x.getLength() - 1));
        for (int i = x.getLength() - 2; i >= 0; i--) {
            if (vectorContents.contains(x.getDataAtAsObject(i))) {
                return i + 1;
            } else {
                vectorContents.add(x.getDataAtAsObject(i));
            }
        }
        return 0;
    }

    @SuppressWarnings("unused")
    protected boolean isIncomparable(RAbstractVector x, byte incomparables, byte fromLast) {
        return incomparables == RRuntime.LOGICAL_TRUE;
    }

    protected boolean empty(RAbstractVector x) {
        return x.getLength() == 0;
    }

    private void initChildren() {
        if (castTypeNode == null) {
            CompilerDirectives.transferToInterpreterAndInvalidate();
            castTypeNode = insert(CastTypeNodeFactory.create(null, null));
            typeof = insert(TypeofNodeFactory.create(null));
        }
    }
}
