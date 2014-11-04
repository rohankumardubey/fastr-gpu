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
public class TestrGenBuiltindigamma extends TestBase {

    @Test
    @Ignore
    public void testdigamma1() {
        assertEval("argv <- list(structure(c(3.80516394437114, 12.8051639443711, 15.8051639443711, 6.80516394437114, 6.80516394437114, 14.8051639443711, 21.8051639443711, 23.8051639443711, 7.80516394437114, 7.80516394437114, 16.8051639443711, 8.80516394437114, 15.8051639443711, 7.80516394437114, 33.8051639443711, 54.8051639443711, 58.8051639443711, 15.8051639443711, 17.8051639443711, 17.8051639443711, 18.8051639443711, 41.8051639443711, 44.8051639443711, 47.8051639443711, 9.80516394437114, 24.8051639443711, 24.8051639443711, 29.8051639443711, 35.8051639443711, 37.8051639443711, 39.8051639443711, 4.80516394437114, 6.80516394437114, 12.8051639443711, 25.8051639443711, 46.8051639443711, 6.80516394437114, 7.80516394437114, 7.80516394437114, 10.8051639443711, 14.8051639443711, 24.8051639443711, 26.8051639443711, 33.8051639443711, 54.8051639443711, 55.8051639443711, 6.80516394437114, 6.80516394437114, 12.8051639443711, 18.8051639443711, 20.8051639443711, 9.80516394437114, 14.8051639443711, 15.8051639443711, 21.8051639443711, 48.8051639443711, 49.8051639443711, 61.8051639443711, 82.8051639443711, 3.80516394437114, 1.80516394437114, 3.80516394437114, 4.80516394437114, 6.80516394437114, 11.8051639443711, 15.8051639443711, 22.8051639443711, 37.8051639443711, 41.8051639443711, 7.80516394437114, 18.8051639443711, 68.8051639443711, 1.80516394437114, 1.80516394437114, 3.80516394437114, 8.80516394437114, 12.8051639443711, 13.8051639443711, 1.80516394437114, 1.80516394437114, 6.80516394437114, 6.80516394437114, 6.80516394437114, 12.8051639443711, 18.8051639443711, 4.80516394437114, 5.80516394437114, 23.8051639443711, 31.8051639443711, 37.8051639443711, 9.80516394437114, 1.80516394437114, 2.80516394437114, 6.80516394437114, 8.80516394437114, 17.8051639443711, 28.8051639443711, 1.80516394437114, 31.8051639443711, 11.8051639443711, 15.8051639443711, 28.8051639443711, 42.8051639443711, 70.8051639443711, 26.8051639443711, 11.8051639443711, 12.8051639443711, 21.8051639443711, 34.8051639443711, 6.80516394437114, 8.80516394437114, 1.80516394437114, 2.80516394437114, 6.80516394437114, 6.80516394437114, 6.80516394437114, 6.80516394437114, 8.80516394437114, 12.8051639443711, 16.8051639443711, 6.80516394437114, 15.8051639443711, 7.80516394437114, 7.80516394437114, 8.80516394437114, 29.8051639443711, 1.80516394437114, 6.80516394437114, 15.8051639443711, 3.80516394437114, 3.80516394437114, 4.80516394437114, 9.80516394437114, 11.8051639443711, 13.8051639443711, 2.80516394437114, 2.80516394437114, 10.8051639443711, 23.8051639443711, 4.80516394437114, 4.80516394437114, 6.80516394437114, 16.8051639443711, 19.8051639443711, 23.8051639443711, 38.8051639443711), .Names = c(\'1\', \'2\', \'3\', \'4\', \'5\', \'6\', \'7\', \'8\', \'9\', \'10\', \'11\', \'12\', \'13\', \'14\', \'15\', \'16\', \'17\', \'18\', \'19\', \'20\', \'21\', \'22\', \'23\', \'24\', \'25\', \'26\', \'27\', \'28\', \'29\', \'30\', \'31\', \'32\', \'33\', \'34\', \'35\', \'36\', \'37\', \'38\', \'39\', \'40\', \'41\', \'42\', \'43\', \'44\', \'45\', \'46\', \'47\', \'48\', \'49\', \'50\', \'51\', \'52\', \'53\', \'54\', \'55\', \'56\', \'57\', \'58\', \'59\', \'60\', \'61\', \'62\', \'63\', \'64\', \'65\', \'66\', \'67\', \'68\', \'69\', \'70\', \'71\', \'72\', \'73\', \'74\', \'75\', \'76\', \'77\', \'78\', \'79\', \'80\', \'81\', \'82\', \'83\', \'84\', \'85\', \'86\', \'87\', \'88\', \'89\', \'90\', \'91\', \'92\', \'93\', \'94\', \'95\', \'96\', \'97\', \'98\', \'99\', \'100\', \'101\', \'102\', \'103\', \'104\', \'105\', \'106\', \'107\', \'108\', \'109\', \'110\', \'111\', \'112\', \'113\', \'114\', \'115\', \'116\', \'117\', \'118\', \'119\', \'120\', \'121\', \'122\', \'123\', \'124\', \'125\', \'126\', \'127\', \'128\', \'129\', \'130\', \'131\', \'132\', \'133\', \'134\', \'135\', \'136\', \'137\', \'138\', \'139\', \'140\', \'141\', \'142\', \'143\', \'144\', \'145\', \'146\')));digamma(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testdigamma2() {
        assertEval("argv <- list(structure(c(9.16602330897621, 9.16602330897621, 1.16602330897621, 1.16602330897621, 3.16602330897621, 3.16602330897621, 6.16602330897621, 6.16602330897621, 6.16602330897621, 6.16602330897621, 2.16602330897621, 2.16602330897621, 8.16602330897621, 8.16602330897621, 1.16602330897621, 1.16602330897621, 7.16602330897621, 7.16602330897621, 19.1660233089762, 19.1660233089762, 2.16602330897621, 2.16602330897621), .Names = c(\'1\', \'1.1\', \'2\', \'2.1\', \'3\', \'3.1\', \'4\', \'4.1\', \'5\', \'5.1\', \'6\', \'6.1\', \'7\', \'7.1\', \'8\', \'8.1\', \'9\', \'9.1\', \'10\', \'10.1\', \'11\', \'11.1\')));digamma(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testdigamma3() {
        assertEval("argv <- list(FALSE);digamma(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testdigamma4() {
        assertEval("argv <- list(structure(numeric(0), .Dim = c(0L, 0L)));digamma(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testdigamma5() {
        assertEval("argv <- list(c(1e+30, 1e+60, 1e+90, 1e+120, 1e+150, 1e+180, 1e+210, 1e+240, 1e+270, 1e+300));digamma(argv[[1]]);");
    }
}
