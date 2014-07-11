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
package com.oracle.truffle.r.nodes.function;

import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.runtime.*;

/**
 * Simple container class for holding 'evaluated' arguments ({@link #getEvaluatedArgs()}) which are
 * ready to be pushed into {@link RArguments}. Objects of this class are created by
 * {@link MatchedArgumentsNode}!
 *
 * @see #getNames()
 */
public class EvaluatedArguments extends Arguments<RNode> {

    EvaluatedArguments(RNode[] evaluatedArgs, String[] names) {
        super(evaluatedArgs, names);
    }

    /**
     * @return The argument array that contains the evaluated arguments
     * @see EvaluatedArguments
     */
    public RNode[] getEvaluatedArgs() {
        return arguments;
    }

    /**
     * @return The names of the arguments that where supplied for the function, in the order the
     *         function call specifies (NOT formal order)
     * @see EvaluatedArguments
     */
    public String[] getNames() {
        return names;
    }
}
