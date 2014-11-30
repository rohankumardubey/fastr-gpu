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

// Checkstyle: stop line length check

                                                                 public class TestrGenBuiltinsubstrassign_ extends TestBase {

	@Test
    @Ignore
	public void testsubstrassign_1() {
		assertEval("argv <- structure(list(x = c(\'NA\', NA, \'BANANA\'), start = 1,     stop = 2, value = \'na\'), .Names = c(\'x\', \'start\', \'stop\',     \'value\'));"+
			"do.call(\'substr<-\', argv)");
	}


	@Test
    @Ignore
	public void testsubstrassign_2() {
		assertEval("argv <- structure(list(x = \'abcde\', start = NA, stop = 3, value = \'abc\'),     .Names = c(\'x\', \'start\', \'stop\', \'value\'));"+
			"do.call(\'substr<-\', argv)");
	}

}

