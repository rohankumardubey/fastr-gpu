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

public class TestrGenBuiltindiagassign_ extends TestBase {

    @Test
    public void testdiagassign_1() {
        assertEval(Ignored.Unknown, "argv <- structure(list(x = structure(numeric(0), .Dim = c(0L,     4L)), value = numeric(0)), .Names = c('x', 'value'));do.call('diag<-', argv)");
    }

    @Test
    public void testdiagassign_3() {
        assertEval(Ignored.Unknown,
                        "argv <- structure(list(x = structure(c(0, 0, 0, 0, 0, 0, 0.215098376664487,     0, 0, 0, -1.65637081299852, 0, 0, 0, 0, 0, -0.414332953459613,     0, 0, -1.40806198482254, 0, 0, 0, 0, 0, 0, 0, 0, -0.856525152312943,     0, 0, 0, 0, 0, 0, 0, 0, -0.0763379828255197, 0, 0, 0, 0,     0, 0, 0, 0, 0, 0, 0.566886579526715, 0, 0, 0, 0, 0, 0.6662762965807,     -1.0108032000459, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1.27827012429586,     0, 0, 0, 0, 0, 2.58429591514953, 0, 0, 2.11417636787577,     -0.433540727336897, 0, -1.2168073873217, 0, 0, 0, 0, 0, 0,     0, 0, 0, 0, -0.739899226286947, 0, 1.63831140634344, 0.940796284653334,     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1.27827012429586, 0, 0,     0, -1.53221105110478, 0, 0.0842634801991399, 0, 0, 0, 0,     0, 0, 0, 0.46436714586526, 0, 0, 0.215098376664487, 0, 0,     0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.20702771149749, 0, 0,     0, 0, 0, 0, 0, 0, 0, 0, -1.53221105110478, 0, 0, 0, 0, 0,     0, 0, 0, 0, 0, 0, 0, 0.797128455296496, 0, 0, -0.856525152312943,     0.566886579526715, 0, -0.739899226286947, 0, 0, 0, 0, 0,     0, 0, 0, 0, 0, 0, -0.53717285365944, 0, 0, -1.78309634885893,     0, 0, 0, 0, 0, 0.0842634801991399, 0, 0, 0, 0, 0, 0, 0, 0,     0, 0, 0.0654058852501743, 0, 0, 0, -1.65637081299852, 0,     0, 0, 1.63831140634344, 0, 0, 0, 0, 0, 0, -0.211859819992765,     1.70777682244235, 0, 0, 0.899304333370124, 0, 0, 0.696790958441438,     0, 0, 0, 0, 2.58429591514953, 0.940796284653334, 0, 0, 0,     0, 0, -0.211859819992765, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,     0, 0, 0, 0, 0, 0, 0, 0, 1.70777682244235, 0, 0, 0, 0, 0,     0, 2.70925832108517, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,     0, 0, 0, 0, 0, 0, 0, 0, 0, -0.218019634714546, 0, 0, 0.6662762965807,     2.11417636787577, 0, 0, 1.20702771149749, 0, 0, 0, 0, 0,     0, 0, 0, 0, 0, 0, -0.431663950618028, 0, 0, 0, -1.0108032000459,     -0.433540727336897, 0, 0, 0, 0, 0, 0, 0.899304333370124,     0, 0, 0, 0, 0, 1.84823959064644, 0, 0, 0, -0.414332953459613,     0, 0, 0, 0, 0, 0, 0, -0.53717285365944, 0.0654058852501743,     0, 0, 0, 0, 0, 1.84823959064644, 0.487718131057368, 0, 0,     0, 0, -0.0763379828255197, 0, -1.2168073873217, 0, 0.46436714586526,     0, 0, 0, 0, 0, 0, 2.70925832108517, 0, 0, 0, 0, 0, 0, 0.89955019874646,     0, 0, 0, 0, 0, 0, 0, 0.797128455296496, 0, 0, 0.696790958441438,     0, 0, 0, -0.431663950618028, 0, 0, 0, 0, 0, -1.40806198482254,     0, 0, 0, 0, 0, 0, 0, -1.78309634885893, 0, 0, 0, 0, -0.218019634714546,     0, 0, 0, 0.89955019874646, 0, 0), .Dim = c(20L, 20L)), value = 1),     .Names = c('x', 'value'));"
                                        + "do.call('diag<-', argv)");
    }

}
