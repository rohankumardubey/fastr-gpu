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
package com.oracle.truffle.r.test.testrgen;

import org.junit.*;

import com.oracle.truffle.r.test.*;

// Checkstyle: stop line length check
public class TestrGenBuiltintrigamma extends TestBase {

    @Test
    @Ignore
    public void testtrigamma1() {
        assertEval("argv <- list(structure(c(9.16602362697115, 1.16602362697115, 3.16602362697115, 6.16602362697115, 6.16602362697115, 2.16602362697115, 8.16602362697115, 1.16602362697115, 7.16602362697115, 19.1660236269712, 2.16602362697115), .Names = c(\'1\', \'2\', \'3\', \'4\', \'5\', \'6\', \'7\', \'8\', \'9\', \'10\', \'11\')));trigamma(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testtrigamma2() {
        assertEval("argv <- list(structure(c(3.23454845691922, 12.2345484569192, 15.2345484569192, 6.23454845691922, 6.23454845691922, 14.2345484569192, 21.2345484569192, 23.2345484569192, 7.23454845691922, 7.23454845691922, 16.2345484569192, 8.23454845691922, 15.2345484569192, 7.23454845691922, 33.2345484569192, 54.2345484569192, 58.2345484569192, 15.2345484569192, 17.2345484569192, 17.2345484569192, 18.2345484569192, 41.2345484569192, 44.2345484569192, 47.2345484569192, 9.23454845691922, 24.2345484569192, 24.2345484569192, 29.2345484569192, 35.2345484569192, 37.2345484569192, 39.2345484569192, 4.23454845691922, 6.23454845691922, 12.2345484569192, 25.2345484569192, 46.2345484569192, 6.23454845691922, 7.23454845691922, 7.23454845691922, 10.2345484569192, 14.2345484569192, 24.2345484569192, 26.2345484569192, 33.2345484569192, 54.2345484569192, 55.2345484569192, 6.23454845691922, 6.23454845691922, 12.2345484569192, 18.2345484569192, 20.2345484569192, 9.23454845691922, 14.2345484569192, 15.2345484569192, 21.2345484569192, 48.2345484569192, 49.2345484569192, 61.2345484569192, 82.2345484569192, 3.23454845691922, 1.23454845691922, 3.23454845691922, 4.23454845691922, 6.23454845691922, 11.2345484569192, 15.2345484569192, 22.2345484569192, 37.2345484569192, 41.2345484569192, 7.23454845691922, 18.2345484569192, 68.2345484569192, 1.23454845691922, 1.23454845691922, 3.23454845691922, 8.23454845691922, 12.2345484569192, 13.2345484569192, 1.23454845691922, 1.23454845691922, 6.23454845691922, 6.23454845691922, 6.23454845691922, 12.2345484569192, 18.2345484569192, 4.23454845691922, 5.23454845691922, 23.2345484569192, 31.2345484569192, 37.2345484569192, 9.23454845691922, 1.23454845691922, 2.23454845691922, 6.23454845691922, 8.23454845691922, 17.2345484569192, 28.2345484569192, 1.23454845691922, 31.2345484569192, 11.2345484569192, 15.2345484569192, 28.2345484569192, 42.2345484569192, 70.2345484569192, 26.2345484569192, 11.2345484569192, 12.2345484569192, 21.2345484569192, 34.2345484569192, 6.23454845691922, 8.23454845691922, 1.23454845691922, 2.23454845691922, 6.23454845691922, 6.23454845691922, 6.23454845691922, 6.23454845691922, 8.23454845691922, 12.2345484569192, 16.2345484569192, 6.23454845691922, 15.2345484569192, 7.23454845691922, 7.23454845691922, 8.23454845691922, 29.2345484569192, 1.23454845691922, 6.23454845691922, 15.2345484569192, 3.23454845691922, 3.23454845691922, 4.23454845691922, 9.23454845691922, 11.2345484569192, 13.2345484569192, 2.23454845691922, 2.23454845691922, 10.2345484569192, 23.2345484569192, 4.23454845691922, 4.23454845691922, 6.23454845691922, 16.2345484569192, 19.2345484569192, 23.2345484569192, 38.2345484569192), .Names = c(\'1\', \'2\', \'3\', \'4\', \'5\', \'6\', \'7\', \'8\', \'9\', \'10\', \'11\', \'12\', \'13\', \'14\', \'15\', \'16\', \'17\', \'18\', \'19\', \'20\', \'21\', \'22\', \'23\', \'24\', \'25\', \'26\', \'27\', \'28\', \'29\', \'30\', \'31\', \'32\', \'33\', \'34\', \'35\', \'36\', \'37\', \'38\', \'39\', \'40\', \'41\', \'42\', \'43\', \'44\', \'45\', \'46\', \'47\', \'48\', \'49\', \'50\', \'51\', \'52\', \'53\', \'54\', \'55\', \'56\', \'57\', \'58\', \'59\', \'60\', \'61\', \'62\', \'63\', \'64\', \'65\', \'66\', \'67\', \'68\', \'69\', \'70\', \'71\', \'72\', \'73\', \'74\', \'75\', \'76\', \'77\', \'78\', \'79\', \'80\', \'81\', \'82\', \'83\', \'84\', \'85\', \'86\', \'87\', \'88\', \'89\', \'90\', \'91\', \'92\', \'93\', \'94\', \'95\', \'96\', \'97\', \'98\', \'99\', \'100\', \'101\', \'102\', \'103\', \'104\', \'105\', \'106\', \'107\', \'108\', \'109\', \'110\', \'111\', \'112\', \'113\', \'114\', \'115\', \'116\', \'117\', \'118\', \'119\', \'120\', \'121\', \'122\', \'123\', \'124\', \'125\', \'126\', \'127\', \'128\', \'129\', \'130\', \'131\', \'132\', \'133\', \'134\', \'135\', \'136\', \'137\', \'138\', \'139\', \'140\', \'141\', \'142\', \'143\', \'144\', \'145\', \'146\')));trigamma(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testtrigamma3() {
        assertEval("argv <- list(c(1e+30, 1e+60, 1e+90, 1e+120, 1e+150, 1e+180, 1e+210, 1e+240, 1e+270, 1e+300));trigamma(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testtrigamma4() {
        assertEval("argv <- list(c(-100, -3, -2, -1, 0, 1, 2, -99.9, -7.7, -3, -2.9, -2.8, -2.7, -2.6, -2.5, -2.4, -2.3, -2.2, -2.1, -2, -1.9, -1.8, -1.7, -1.6, -1.5, -1.4, -1.3, -1.2, -1.1, -1, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.0999999999999996, 0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 3, 5.1, 77));trigamma(argv[[1]]);");
    }
}

