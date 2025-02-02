/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Copyright (c) 2014, Purdue University
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates
 *
 * All rights reserved.
 */

package com.oracle.truffle.r.nodes.unary;

import java.util.HashMap;
import java.util.Map;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.TypeSystemReference;
import com.oracle.truffle.api.utilities.ConditionProfile;
import com.oracle.truffle.r.nodes.function.ClassHierarchyNode;
import com.oracle.truffle.r.nodes.function.ClassHierarchyNodeGen;
import com.oracle.truffle.r.runtime.RRuntime;
import com.oracle.truffle.r.runtime.data.RAttributeProfiles;
import com.oracle.truffle.r.runtime.data.RStringVector;
import com.oracle.truffle.r.runtime.data.RTypes;
import com.oracle.truffle.r.runtime.data.model.RAbstractStringVector;
import com.oracle.truffle.r.runtime.nodes.RBaseNode;

/**
 * Basic support for "inherits" that is used by the {@code inherits} builtin and others.
 */
@TypeSystemReference(RTypes.class)
public abstract class InheritsNode extends RBaseNode {

    private final ConditionProfile sizeOneProfile = ConditionProfile.createBinaryProfile();
    protected final RAttributeProfiles attrProfiles = RAttributeProfiles.create();

    public abstract byte execute(Object x, Object what);

    protected ClassHierarchyNode createClassHierarchy() {
        return ClassHierarchyNodeGen.create(true);
    }

    @Specialization
    protected byte doesInherit(Object x, RAbstractStringVector what, @Cached("createClassHierarchy()") ClassHierarchyNode classHierarchy) {
        RStringVector hierarchy = classHierarchy.execute(x);
        if (sizeOneProfile.profile(what.getLength() == 1)) {
            String whatString = what.getDataAt(0);
            for (int i = 0; i < hierarchy.getLength(); i++) {
                if (whatString.equals(hierarchy.getDataAt(i))) {
                    return RRuntime.LOGICAL_TRUE;
                }
            }
        } else {
            Map<String, Integer> classToPos = initClassToPos(hierarchy);
            for (int i = 0; i < what.getLength(); i++) {
                if (classToPos.get(what.getDataAt(i)) != null) {
                    return RRuntime.LOGICAL_TRUE;
                }
            }
        }
        return RRuntime.LOGICAL_FALSE;
    }

    // map operations lead to recursion resulting in compilation failure
    @TruffleBoundary
    public static HashMap<String, Integer> initClassToPos(RStringVector classHr) {
        // Create a mapping for elements to their respective positions
        // in the vector for faster lookup.
        HashMap<String, Integer> classToPos = new HashMap<>(classHr.getLength());
        for (int i = 0; i < classHr.getLength(); i++) {
            classToPos.put(classHr.getDataAt(i), i);
        }
        return classToPos;
    }
}
