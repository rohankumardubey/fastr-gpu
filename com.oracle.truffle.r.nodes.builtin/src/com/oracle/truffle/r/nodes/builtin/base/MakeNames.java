/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
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

import static com.oracle.truffle.r.runtime.RBuiltinKind.INTERNAL;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.utilities.ConditionProfile;
import com.oracle.truffle.r.nodes.builtin.RBuiltinNode;
import com.oracle.truffle.r.nodes.unary.CastLogicalNode;
import com.oracle.truffle.r.nodes.unary.CastLogicalNodeGen;
import com.oracle.truffle.r.nodes.unary.CastNode;
import com.oracle.truffle.r.nodes.unary.CastToVectorNode;
import com.oracle.truffle.r.nodes.unary.CastToVectorNodeGen;
import com.oracle.truffle.r.runtime.RBuiltin;
import com.oracle.truffle.r.runtime.RError;
import com.oracle.truffle.r.runtime.RRuntime;
import com.oracle.truffle.r.runtime.Utils;
import com.oracle.truffle.r.runtime.data.RStringVector;
import com.oracle.truffle.r.runtime.data.model.RAbstractLogicalVector;
import com.oracle.truffle.r.runtime.data.model.RAbstractStringVector;
import com.oracle.truffle.r.runtime.nodes.RBaseNode;
import com.oracle.truffle.r.runtime.ops.na.NACheck;

@RBuiltin(name = "make.names", kind = INTERNAL, parameterNames = {"names", "allow_"})
public abstract class MakeNames extends RBuiltinNode {

    private final ConditionProfile namesLengthZero = ConditionProfile.createBinaryProfile();
    private final NACheck dummyCheck = new NACheck(); // never triggered (used for vector update)

    @Override
    protected void createCasts(CastBuilder casts) {
        casts.custom(1, new AllowUnderscoreConverter());
    }

    @TruffleBoundary
    private static String concat(String s1, String s2) {
        return s1 + s2;
    }

    private static String getKeyword(String s, boolean allowUnderscore) {
        // verbose but avoids String concatenation on the slow path
        if (s.equals("if")) {
            return "if.";
        } else if (s.equals("else")) {
            return "else.";
        } else if (s.equals("repeat")) {
            return "repeat.";
        } else if (s.equals("while")) {
            return "while.";
        } else if (s.equals("function")) {
            return "function.";
        } else if (s.equals("for")) {
            return "for.";
        } else if (s.equals("in")) {
            return "in.";
        } else if (s.equals("next")) {
            return "next.";
        } else if (s.equals("break")) {
            return "break.";
        } else if (s.equals("TRUE")) {
            return "TRUE.";
        } else if (s.equals("FALSE")) {
            return "FALSE.";
        } else if (s.equals("NULL")) {
            return "NULL.";
        } else if (s.equals("Inf")) {
            return "Inf.";
        } else if (s.equals("NaN")) {
            return "NaN.";
        } else if (s.equals("NA")) {
            return "NA.";
        } else if (s.equals("NA_integer_")) {
            return allowUnderscore ? "NA_integer_." : "NA.integer.";
        } else if (s.equals("NA_real_")) {
            return allowUnderscore ? "NA_real_." : "NA.real.";
        } else if (s.equals("NA_complex_")) {
            return allowUnderscore ? "NA_complex_." : "NA.complex.";
        } else if (s.equals("NA_character_")) {
            return allowUnderscore ? "NA_character_." : "NA.character.";
        } else {
            return null;
        }
    }

    private static RStringVector getNewNames(RAbstractStringVector names, RStringVector newNames) {
        RStringVector ret = newNames;
        if (ret == null) {
            ret = names.materialize();
            if (ret.isShared()) {
                ret = (RStringVector) ret.copy();
            }
        }
        return ret;
    }

    private static char[] getNameArray(String name, char[] nameArray) {
        char[] newNameArray = nameArray;
        if (newNameArray == null) {
            newNameArray = name.toCharArray();
        }
        return newNameArray;
    }

    private static String getName(String name, boolean allowUnderscore) {
        if (name.length() == 0) {
            return "X";
        }
        String newName = name;
        char[] nameArray = null;
        // if necessary, replace invalid characters with .
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Utils.isIsoLatinDigit(c) && !Character.isLetter(c) && (!allowUnderscore || c != '_')) {
                nameArray = getNameArray(name, nameArray);
                nameArray[i] = '.';
            }
        }
        if (nameArray != null) {
            newName = new String(nameArray);
        }
        if (Utils.isIsoLatinDigit(newName.charAt(0)) || (newName.length() > 1 && newName.charAt(0) == '.' && Utils.isIsoLatinDigit(newName.charAt(1))) ||
                        (newName.charAt(0) == '.' && name.charAt(0) != '.')) {
            newName = concat("X", newName);
        }

        return newName;
    }

    @Specialization
    protected RAbstractStringVector makeNames(RAbstractStringVector names, byte allowUnderScoreArg) {
        if (namesLengthZero.profile(names.getLength() == 0)) {
            return names;
        } else {
            boolean allowUnderscore = allowUnderScoreArg == RRuntime.LOGICAL_TRUE;
            RStringVector newNames = null;
            for (int i = 0; i < names.getLength(); i++) {
                String name = names.getDataAt(i);
                String newName = getKeyword(name, allowUnderscore);
                if (newName != null) {
                    newNames = getNewNames(names, newNames);
                    newNames.updateDataAt(i, newName, dummyCheck);
                } else {
                    newName = getName(name, allowUnderscore);
                    if (newName != name) {
                        // getName returns "name" in case nothing's changed
                        newNames = getNewNames(names, newNames);
                        newNames.updateDataAt(i, newName, dummyCheck);
                    }
                }
            }
            return newNames != null ? newNames : names;
        }
    }

    @Specialization(guards = "!wrongAllowUnderscore(allowUnderScoreArg)")
    protected RAbstractStringVector makeNames(RAbstractStringVector names, RAbstractLogicalVector allowUnderScoreArg) {
        return makeNames(names, allowUnderScoreArg.getDataAt(0));
    }

    @SuppressWarnings("unused")
    @Specialization(guards = "wrongAllowUnderscore(allowUnderScoreArg)")
    protected RAbstractStringVector makeNamesWrongUnderscoreEmpty(RAbstractStringVector names, RAbstractLogicalVector allowUnderScoreArg) {
        throw invalidAllowValue(this);
    }

    protected static boolean wrongAllowUnderscore(RAbstractLogicalVector allowUnderScoreArg) {
        return allowUnderScoreArg.getLength() == 0 || RRuntime.isNA(allowUnderScoreArg.getDataAt(0));
    }

    protected static final class AllowUnderscoreConverter extends CastNode {

        @Child private CastLogicalNode castLogical = CastLogicalNodeGen.create(false, false, false);
        @Child private CastToVectorNode castVector = CastToVectorNodeGen.create(false);

        @Override
        public RAbstractLogicalVector execute(Object value) {
            try {
                // TODO Catching RError!
                return (RAbstractLogicalVector) castLogical.execute(castVector.execute(value));
            } catch (RError x) {
                throw invalidAllowValue(this);
            }
        }
    }

    private static RError invalidAllowValue(RBaseNode invokingNode) throws RError {
        throw RError.error(invokingNode, RError.Message.INVALID_VALUE, "allow_");
    }
}
