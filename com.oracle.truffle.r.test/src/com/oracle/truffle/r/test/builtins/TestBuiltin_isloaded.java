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
package com.oracle.truffle.r.test.builtins;

import org.junit.Test;

import com.oracle.truffle.r.test.TestBase;

// Checkstyle: stop line length check
public class TestBuiltin_isloaded extends TestBase {

    @Test
    public void testisloaded1() {
        assertEval("argv <- list('PDF', '', 'External'); .Internal(is.loaded(argv[[1]], argv[[2]], argv[[3]]))");
    }

    @Test
    public void testisloaded2() {
        assertEval("argv <- list('supsmu', '', ''); .Internal(is.loaded(argv[[1]], argv[[2]], argv[[3]]))");
    }
}
