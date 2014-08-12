/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 * 
 * Copyright (c) 2014, Purdue University
 * Copyright (c) 2014, Oracle and/or its affiliates
 * All rights reserved.
 */
package com.oracle.truffle.r.test.testrgen;

import org.junit.*;

import com.oracle.truffle.r.test.*;

public class TestrGenBuiltinutf8ToInt extends TestBase {

    @Test
    @Ignore
    public void testutf8ToInt1() {
        assertEval("argv <- list(\'lasy\'); .Internal(utf8ToInt(argv[[1]]))");
    }
}

