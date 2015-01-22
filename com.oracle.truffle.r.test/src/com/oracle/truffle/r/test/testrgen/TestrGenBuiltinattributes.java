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
package com.oracle.truffle.r.test.testrgen;

import org.junit.*;

import com.oracle.truffle.r.test.*;

// Checkstyle: stop line length check
public class TestrGenBuiltinattributes extends TestBase {

    @Test
    public void testattributes1() {
        assertEval("argv <- list(structure(c(5.79821692617331, 1.82341879820553, 2.78390295547843, 5.76851897647876, 1.96728131351224, 1.64012180629841, 0.76150764829566, 8.78324957466388, 0.711713280005232, 0.0432245134694077, 0.484038236738706, 2.2604286525194), .Names = c(\'1\', \'3\', \'5\', \'7\', \'9\', \'11\', \'13\', \'15\', \'17\', \'19\', \'21\', \'23\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes2() {
        assertEval("argv <- list(1386518010.66723);attributes(argv[[1]]);");
    }

    @Test
    public void testattributes3() {
        assertEval("argv <- list(structure(list(x = c(55, 55.4, 55.8, 56.2, 56.6, 57, 57.4, 57.8, 58.2, 58.6, 59, 59.4, 59.8, 60.2, 60.6, 61, 61.4, 61.8, 62.2, 62.6, 63, 63.4, 63.8, 64.2, 64.6, 65, 65.4, 65.8, 66.2, 66.6, 67, 67.4, 67.8, 68.2, 68.6, 69, 69.4, 69.8, 70.2, 70.6, 71, 71.4, 71.8, 72.2, 72.6, 73, 73.4, 73.8, 74.2, 74.6, 75), y = c(NA, NA, NA, NA, NA, NA, NA, NA, 115.348528272371, 116.097056544742, 117, 118.121358638144, 119.369358638144, 120.616754451341, 121.816150264538, 123, 124.196077478756, 125.399549206385, 126.598034046405, 127.793047158796, 129, 130.223726064439, 131.424330251242, 132.555722198323, 133.686509958601, 135, 136.60008742339, 138.253074310999, 139.654076730115, 140.802092261622, 142, 143.481497863021, 145.150710102743, 146.833352023286, 148.446781704108, 150, 151.524882658905, 153.128867127397, 154.93285159589, 156.93285159589, 159, 161.026859361644, 163.013429680822, NA, NA, NA, NA, NA, NA, NA, NA)), .Names = c(\'x\', \'y\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes4() {
        assertEval("argv <- list(structure(c(1+1i, 2+1.4142135623731i, 3+1.73205080756888i, 4+2i, 5+2.23606797749979i, 6+2.44948974278318i, 7+2.64575131106459i, 8+2.82842712474619i, 9+3i, 10+3.1622776601684i), id = character(0), class = structure(\'withId\', package = \'.GlobalEnv\')));attributes(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testattributes5() {
        assertEval("argv <- list(structure(c(\'o\', \'p\', \'v\', \'i\', \'r\', \'w\', \'b\', \'m\', \'f\', \'s\'), date = structure(1224086400, class = c(\'POSIXct\', \'POSIXt\'), tzone = \'\'), .S3Class = \'stamped\', class = structure(\'stamped\', package = \'.GlobalEnv\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes6() {
        assertEval("argv <- list(structure(list(loc = c(0.0804034870161223, 10.3548347412639), cov = structure(c(3.01119301965569, 6.14320559215603, 6.14320559215603, 14.7924762275451), .Dim = c(2L, 2L)), d2 = 2, wt = c(0, 0, 0, 0, 0, 1.75368801162502e-134, 0, 0, 0, 2.60477585273833e-251, 1.16485035372295e-260, 0, 1.53160350210786e-322, 0.333331382328728, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3.44161262707711e-123, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1.968811545398e-173, 0, 8.2359965384697e-150, 0, 0, 0, 0, 6.51733217171341e-10, 0, 2.36840184577368e-67, 0, 9.4348408357524e-307, 0, 1.59959906013771e-89, 0, 8.73836857865034e-286, 7.09716190970992e-54, 0, 0, 0, 1.530425353017e-274, 8.57590058044551e-14, 0.333333106397154, 0, 0, 1.36895217898448e-199, 2.0226102635783e-177, 5.50445388209462e-42, 0, 0, 0, 0, 1.07846402051283e-44, 1.88605464411243e-186, 1.09156111051203e-26, 0, 3.0702877273237e-124, 0.333333209689785, 0, 0, 0, 0, 0, 0, 3.09816093866831e-94, 0, 0, 4.7522727332095e-272, 0, 0, 2.30093251441394e-06, 0, 0, 1.27082826644707e-274, 0, 0, 0, 0, 0, 0, 0, 4.5662025456054e-65, 0, 2.77995853978268e-149, 0, 0, 0), sqdist = c(0.439364946869246, 0.0143172566761092, 0.783644692619938, 0.766252947443554, 0.346865912102713, 1.41583192825661, 0.168485512965902, 0.354299830956879, 0.0943280426627965, 1.05001058449122, 1.02875556201707, 0.229332323173361, 0.873263925064789, 2.00000009960498, 0.449304354954282, 0.155023307933165, 0.118273979375253, 0.361693898800799, 0.21462398586105, 0.155558909016629, 0.471723661454506, 0.719528696331092, 0.0738164380664225, 1.46001193111051, 0.140785322548143, 0.127761195166703, 0.048012401156175, 0.811750426884519, 0.425827709817574, 0.163016638545231, 0.557810866640707, 0.277350147637843, 0.0781399119055092, 1.29559183995835, 0.718376405567138, 1.37650242941478, 0.175087780508154, 0.233808973148729, 0.693473805463067, 0.189096604125073, 1.96893781800017, 0.4759756980592, 1.69665760380474, 0.277965749373647, 0.920525436884815, 0.57525234053591, 1.59389578665009, 0.175715364671313, 0.972045794851437, 1.75514684962809, 0.0597413185507202, 0.174340343040626, 0.143421553552865, 0.997322770596838, 1.94096736957465, 2.00000001159796, 0.367000821772989, 0.682474530588235, 1.20976163307984, 1.27031685239035, 1.79775635513363, 0.0857761902860323, 0.435578932929501, 0.214370604878221, 0.494714247412686, 1.78784623754399, 1.24216674083069, 1.87749485326709, 0.0533296334123023, 1.45588362584438, 2.00000000631459, 0.208857144738039, 0.119251291573058, 0.365303924649962, 0.690656674239668, 0.0396958405786268, 0.258262120876164, 1.57360254057537, 0.307548421049514, 0.628417063100241, 1.00647098749202, 0.297624360530352, 0.400289147351669, 1.98298426250944, 0.129127182829694, 0.0794695319493149, 0.991481735944321, 0.444068154119836, 0.206790162395106, 0.574310829851377, 0.181887577583334, 0.433872021297517, 0.802994892604009, 0.293053770941001, 1.7002969001965, 0.77984639982848, 1.36127407487932, 0.761935213110323, 0.597915313430067, 0.237134831067472), prob = NULL, tol = 1e-07, eps = 9.96049758228423e-08, it = 898L, maxit = 5000,     ierr = 0L, conv = TRUE), .Names = c(\'loc\', \'cov\', \'d2\', \'wt\', \'sqdist\', \'prob\', \'tol\', \'eps\', \'it\', \'maxit\', \'ierr\', \'conv\'), class = \'ellipsoid\'));attributes(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testattributes7() {
        assertEval("argv <- list(NULL);attributes(argv[[1]]);");
    }

    @Test
    public void testattributes8() {
        assertEval("argv <- list(\'Error in setClass(\\\'class3\\\', representation(\\\'class1\\\', \\\'class2\\\')) : \\n  error in contained classes (\\\'class2\\\') for class “class3”; class definition removed from ‘.GlobalEnv’\\n\');attributes(argv[[1]]);");
    }

    @Test
    public void testattributes9() {
        assertEval("argv <- list(structure(list(), .Names = character(0), row.names = integer(0), .S3Class = \'data.frame\', extra = character(0)));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes10() {
        assertEval("argv <- list(structure(list(zi.si. = c(-2.73014251717135, -2.16787987308811, -1.61026765290054, -1.06093652746977, -0.523224065200069, 0, 0.506450782357207, 0.994479058519472, 1.46306067722175, 1.91173866627745, 2.34053598638487, 2.74985599456053)), .Names = \'zi.si.\', row.names = c(NA, -12L), class = \'data.frame\'));attributes(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testattributes11() {
        assertEval("argv <- list(structure(c(4L, 5L, 1L, 5L, 3L, 4L, 5L, 3L, 2L, 4L), .Label = c(\'a\', \'c\', \'i\', \'s\', \'t\'), class = \'factor\', contrasts = structure(c(1, 0, 0, 0, -1, 0, 1, 0, 0, -1, -0.247125681008604, -0.247125681008604, -0.149872105789645, 0.891249148815458, -0.247125681008604, 0.268816352031209, 0.268816352031209, -0.881781351530059, 0.0753322954364324, 0.268816352031209), .Dim = c(5L, 4L), .Dimnames = list(c(\'a\', \'c\', \'i\', \'s\', \'t\'), NULL))));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes12() {
        assertEval("argv <- list(structure(character(0), .Names = character(0), package = character(0), class = structure(\'signature\', package = \'methods\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes13() {
        assertEval("argv <- list(c(FALSE, FALSE, TRUE, FALSE, TRUE, FALSE, FALSE, FALSE));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes14() {
        assertEval("argv <- list(structure(list(a_string = c(\'foo\', \'bar\'), a_bool = FALSE, a_struct = structure(list(a = 1, b = structure(c(1, 3, 2, 4), .Dim = c(2L, 2L)), c = \'foo\'), .Names = c(\'a\', \'b\', \'c\')), a_cell = structure(list(1, \'foo\', structure(c(1, 3, 2, 4), .Dim = c(2L, 2L)), \'bar\'), .Dim = c(2L, 2L)), a_complex_scalar = 0+1i, a_list = list(1, structure(c(1, 3, 2, 4), .Dim = c(2L, 2L)), \'foo\'), a_complex_matrix = structure(c(1+2i, 5+0i, 3-4i, -6+0i), .Dim = c(2L, 2L)), a_range = c(1, 2, 3, 4, 5), a_scalar = 1,     a_complex_3_d_array = structure(c(1+1i, 3+1i, 2+1i, 4+1i, 5-1i, 7-1i, 6-1i, 8-1i), .Dim = c(2L, 2L, 2L)), a_3_d_array = structure(c(1, 3, 2, 4, 5, 7, 6, 8), .Dim = c(2L, 2L, 2L)), a_matrix = structure(c(1, 3, 2, 4), .Dim = c(2L, 2L)), a_bool_matrix = structure(c(TRUE, FALSE, FALSE, TRUE), .Dim = c(2L, 2L))), .Names = c(\'a_string\', \'a_bool\', \'a_struct\', \'a_cell\', \'a_complex_scalar\', \'a_list\', \'a_complex_matrix\', \'a_range\', \'a_scalar\', \'a_complex_3_d_array\', \'a_3_d_array\', \'a_matrix\', \'a_bool_matrix\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes15() {
        assertEval("argv <- list(structure(c(1L, NA, 3L), .Label = c(\'1\', \'2\', NA)));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes16() {
        assertEval("argv <- list(structure(1, .Dim = c(1L, 1L), a = c(NA, 3, -1, 2), class = structure(\'B\', package = \'.GlobalEnv\')));attributes(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testattributes17() {
        assertEval("argv <- list(structure(list(L = structure(c(\'Min.   :14.00  \', \'1st Qu.:26.00  \', \'Median :29.50  \', \'Mean   :36.39  \', \'3rd Qu.:49.25  \', \'Max.   :70.00  \', \'A:9  \', \'B:9  \', NA, NA, NA, NA), .Dim = c(6L, 2L), .Dimnames = list(c(\'\', \'\', \'\', \'\', \'\', \'\'), c(\'    breaks\', \'wool\')), class = \'table\'), M = structure(c(\'Min.   :12.00  \', \'1st Qu.:18.25  \', \'Median :27.00  \', \'Mean   :26.39  \', \'3rd Qu.:33.75  \', \'Max.   :42.00  \', \'A:9  \', \'B:9  \', NA, NA, NA, NA), .Dim = c(6L, 2L), .Dimnames = list(c(\'\', \'\', \'\', \'\', \'\', \'\'), c(\'    breaks\', \'wool\')), class = \'table\'), H = structure(c(\'Min.   :10.00  \', \'1st Qu.:15.25  \', \'Median :20.50  \', \'Mean   :21.67  \', \'3rd Qu.:25.50  \', \'Max.   :43.00  \', \'A:9  \', \'B:9  \', NA, NA, NA, NA), .Dim = c(6L, 2L), .Dimnames = list(c(\'\', \'\', \'\', \'\', \'\', \'\'), c(\'    breaks\', \'wool\')), class = \'table\')), .Dim = 3L, .Dimnames = structure(list(`warpbreaks[, \'tension\']` = c(\'L\', \'M\', \'H\')), .Names = \'warpbreaks[, \\\'tension\\\']\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes18() {
        assertEval("argv <- list(structure(3.14159265358979, comment = \'Start with pi\'));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes19() {
        assertEval("argv <- list(structure(list(school = c(1L, 1L, 2L, 2L, 3L, 3L), class = c(9L, 10L, 9L, 10L, 9L, 10L), score.1 = c(0.487429052428485, 0.738324705129217, 1.51178116845085, 0.389843236411431, 1.12493091814311, -0.0449336090152309), score.2 = c(0.575781351653492, -0.305388387156356, -0.621240580541804, -2.2146998871775, -0.0161902630989461, 0.943836210685299)), .Names = c(\'school\', \'class\', \'score.1\', \'score.2\'), row.names = c(1L, 2L, 5L, 6L, 9L, 10L), class = \'data.frame\', reshapeWide = structure(list(v.names = NULL,     timevar = \'time\', idvar = c(\'school\', \'class\'), times = c(1, 2), varying = structure(c(\'score.1\', \'score.2\'), .Dim = 1:2)), .Names = c(\'v.names\', \'timevar\', \'idvar\', \'times\', \'varying\'))));attributes(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testattributes20() {
        assertEval("argv <- list(structure(c(-0.927486533732408, -0.13045118082262, -1.45825662858798, -1.81793776180039, -1.62313249921887, 1.26834845395072, -0.994859212587552, -0.60437576902456, -1.06120404454492, 0.877124074149983, 0.824787552890982, 0.704014030529726, -0.702775741558964, 0.544818578451748, 1.48964468805446, 0.13432541283722, -0.128923575804159, -0.354052007275421, 1.0711307633823, -1.33461699082502, 0.380875114294515, -1.07786690183251, 0.810306659502442, -2.17501201437322, -0.407604875801864, -0.462999392844433, 0.194880873045067, 2.65407152575046, 0.564656783773942, 0.906337761254496, -0.631531096896236, -1.27819755429098, -0.538330333636237, 1.00231821330914, -0.286364553583536, 0.469006850788945, 1.05885815169124, 1.21503113231427, 1.44216953152086, 0.486588338054406, 0.268701510628534, -0.463411907566429, 1.02436199321322, -1.39004777906874, 0.999688613035661, -0.916808034628566, -0.502852003923634, 0.212379391073633, -0.0134736853666567, 1.31736970279298, -0.0944249611837457, 0.806746464646202, 0.315038937675493, -1.17340599897154, 0.482494016048211, -0.791050343541626, 0.808325470742601, 0.588652339988029, 1.10512245024341, 1.12322151831428, 0.208749082415184, -0.357070741737234, 0.572101015785145, -0.533738015097777, -0.360034768896796, 0.0342776735716719, -1.99368868442296, 2.04525110526828, 0.854582964377424, -0.292897401378698, -1.18433316855268, -0.376789959833897, -0.538288068463758, -0.232305057346106, -0.375870899318979, -0.359017335348666, -0.741816586737615, -0.269774297924449, -0.796951579131833, 0.0323594165086663, 0.439067375500569, 0.466080486767734, -1.49275492910805, 0.947406234969688, 0.182071046143441, 0.445836300099003, 2.02477378068589, -1.70432399838533, -0.440962927710655, 0.831168740087201, 2.13610340066555, -1.8137969168688, 1.22501979912183, 0.795942206968001, 1.87235555725712, -1.26557065145048, 0.378275537740316, 0.789728084675539, 0.99886763527649, 2.29736830042939, -0.00228198643744461, 0.161544616498807, -1.14848457105275, -1.08025155036982, -1.03819987128219, -0.856130938839897, 1.0042164519591, -0.843667376942675, -1.21154108350058, 0.460882948229546, -0.468389916732288, -0.946568852822378, 1.17783540515932, -0.973911234750034, -0.0639785174878419, -1.1045450251553, 0.442020873295079, 0.0831991221894004, 0.269053190969569, 0.901338595939942, -0.086250034518703, 0.478492308026563, -0.925167039266549, -0.658186865278783, 1.0337884593443, -0.434569632901449, 2.038954875659, 0.202381377518746, 0.484762177881311, -0.360074615793248, -0.129300783906607, 0.651643789311553, 0.934922268235395, -0.0934722501709123, -0.0103213567459648, 1.61917070438612, -1.06268414268298, 0.696244014590397, 0.992388229201836, 0.697363816464206, 0.250252166543902, -0.557960198745092, 0.221087284404152, 0.10961920608307, 0.259174711435693, -0.70162900779778, 1.92431317758909, -0.578758205883074, 1.59171854092147, 0.902876390823624, 0.840275033814991, 1.77586917630517, 0.403033282757909, 0.0539091417299491, -1.26615014148617, 0.0148781649233171, 0.256100494565364, -1.01942222514274, 0.549839939376835, -0.724728578424903, 0.151976282801212, 0.326791648750199, 0.748909789058061, -1.85444018168148, 0.0869984536340467, 1.25350587157032, 0.962041461790638, 0.120578653081864, -0.11935793490114, 0.6951267107979, -1.39729724662012, 1.03310979193329, -0.619324332563388, 0.60759513170927, 0.665670243263983, 1.34746787641065, 0.213814029897928, -0.43608749915348, -0.196912890515945, -0.474024803180795, -0.0932220458288109, -0.739450980689706, -0.299196422560384, -0.765918956807232, -0.374980670595253, 0.65328744055362, -0.552631311606602, -1.24532249102801, 1.50941913047883, -1.79189867145273, -0.892183862327233, 1.20950581962313, 0.454528744013934, 0.266817155028672, 1.65771215541012, 1.00893183514602, 0.761621316769915, 0.262896620317128, -0.128241496594234, 0.980274347240293), .Label = structure(list(c(-2.17503193782145, -0.474004879732569), c(-0.97393115819826, -0.0932021223805848), c(-0.468409840180514, 0.266837078476898), c(-0.086269957966929, 0.696263938038623), c(0.268681587180308, 1.02438191666144), c(0.697343893015979, 2.65409144919869)), class = \'shingleLevel\'), class = \'shingle\'));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes21() {
        assertEval("argv <- list(structure(c(82, 82, 82, 75, 63, 50, 43, 35, 35, 54, 54, 54, 39, 36, 0, 0, 57, 57, 57, 51, 45, 45, 39, 39, 36, 32, 25, 25, 25, 25, 32, 32, 59, 74, 74, 71, 71, 71, 71, 71, 71, 71, 73, 73, 73, 71, 71, 75, 75, 63, 62, 60, 57, 49, 49, 52, 57, 61, 62, 66, 66, 62, 61, 61, 72, 72, 78, 78, 78, 71, 71, 74, 74, 64, 62, 62, 73, 73, 69, 69, 69, 69, 64, 63, 62, 56, 46, 44, 44, 44, 44, 44, 44, 44, 44, 44, 59, 65, 65, 65, 61, 56, 53, 52, 51, 51, 49, 49, 49, 49, 0, 0, 44, 44, 40, 28, 27, 25, 24, 24), .Tsp = c(1945, 1974.75, 4), class = \'ts\'));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes22() {
        assertEval("argv <- list(c(1000, 1e+07, 1));attributes(argv[[1]]);");
    }

    @Test
    @Ignore
    public void testattributes23() {
        assertEval("argv <- list(structure(list(), .Names = character(0), arguments = structure(\'object\', simpleOnly = TRUE), signatures = list(), generic = structure(function (object) standardGeneric(\'show\'), generic = structure(\'show\', package = \'methods\'), package = \'methods\', group = list(), valueClass = character(0), signature = structure(\'object\', simpleOnly = TRUE), default = structure(function (object) showDefault(object, FALSE), target = structure(\'ANY\', class = structure(\'signature\', package = \'methods\'), .Names = \'object\', package = \'methods\'), defined = structure(\'ANY\', class = structure(\'signature\', package = \'methods\'), .Names = \'object\', package = \'methods\'), generic = structure(\'show\', package = \'methods\'), class = structure(\'derivedDefaultMethod\', package = \'methods\')), skeleton = quote((function (object) showDefault(object, FALSE))(object)), class = structure(\'standardGeneric\', package = \'methods\')), class = structure(\'listOfMethods\', package = \'methods\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes24() {
        assertEval("argv <- list(structure(list(x = c(1, 1, 1, 1, 1, 1, 1, 1, 1, 1), y = c(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), fac = structure(c(1L, 3L, 2L, 3L, 3L, 1L, 2L, 3L, 2L, 2L), .Label = c(\'A\', \'B\', \'C\'), class = \'factor\')), .Names = c(\'x\', \'y\', \'fac\'), row.names = c(NA, -10L), class = \'data.frame\'));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes25() {
        assertEval("argv <- list(structure(character(0), package = character(0), class = structure(\'ObjectsWithPackage\', package = \'methods\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes26() {
        assertEval("argv <- list(structure(list(qr = structure(c(-561841.166008647, 1.48748911573234e-06, 1.62250974987379e-06, 1.29819733159371e-06, 1.3199650315892e-06, 1.59089005368605e-06, 3.38370366592718e-06, 1.54037267089339e-06, 1.26888887472893e-06, 1.66561998324397e-06, 2.71299853206351e-06, 1.82740883826792e-06, 1.69185183297085e-06, 1.36523405858032e-06, 1.55319864307643e-06, 1.56902110896583e-06, 1.3906190700334e-06, 1.2997165800148e-06, 1.56369334788134e-06, 1.43353743471477e-06, 1.74457009494164e-06, 2.03022219956374e-06, 1.73363224357496e-06, 1.47177180858703e-06, 4.47621920517122e-06, 1.45868913292866e-06, 1.67531329485535e-06, 1.56635043280177e-06, 1.90866628223014e-06, 1.54802991445627e-06, 1.60217310325092e-06, 2.68573152311995e-06, 1.55319864307643e-06, 1.65295348775885e-06, 1.41747488364731e-06, 0.999999999831096, 1.61369967706714e-06, 1.31520838537442e-06, 2.44157411192876e-06, 5.1686927260975e-06, 1.81484917852149e-06, 1.50372096623665e-06, 1.91837965937479e-06, 1.68517783522785e-06, 1.65295348775885e-06, 1.58810634192319e-06, 1.77478134630985e-06, 1.66883236806679e-06, 2.2002369936867e-06, 1.56635043280177e-06, 1.31520838537442e-06, 1.23229813671524e-06, 1.69185183297085e-06, 1.77091892313243e-06, 1.92329229138616e-06, 1.33290617468824e-06, 1.64672765212343e-06, 2.17841592228372e-06, 2.07828144894252e-06, 1.72289757538629e-06, 1.92824285888419e-06, 1.81484917852149e-06, 2.01304088471671e-06, 1.47621155263461e-06, 1.91350448082172e-06, 1.47177180858703e-06, 1.60217310325092e-06, 1.53282794306286e-06, 1.53282794306286e-06, 2.24592152723567e-06, 1.3564992660355e-06, 1.61662040285441e-06, 1.78652203450807e-06, 1.84456798315616e-06, 1.83165369610876e-06, 2.34653452685434e-06, 2.12325719919593e-06, 1.37956761746782e-06, 1.32156208605305e-06, 1.77478134630985e-06, 1.66242607813213e-06, 1.88501398961327e-06, 1.75571762111387e-06, 1.71936342279218e-06, 2.15723147161546e-06, 1.48294715363101e-06, 1.94332712676627e-06, 1.26606598163328e-06, 1.70544122262692e-06, 1.72289757538629e-06, 1.44385821185109e-06, 1.53533052096275e-06, 1.35133818480092e-06, 1.72645361131692e-06, 1.82319335667127e-06, 1.32963523435973e-06, 1.41158914936576e-06, 2.12325719919593e-06, 1.47844654491007e-06, 1.44385821185109e-06, 1472023.85454144, -24.6606754318977, 0.101285140222523, 0.12658801819228, 0.124500435606533, 0.103298237275271, 0.0485666635912155, 0.106685971539808, 0.129511920733888, 0.0986636292980589, 0.0605734308209414, 0.0899284547245126, 0.0971338624854, 0.120372186413924, 0.105804979598362, 0.104738008044408, 0.118174850871225, 0.126440048191751, 0.105094868834514, 0.114636830861078, 0.0941986135491651, 0.0809448199785816, 0.0947929347785712, 0.111658733889352, 0.0367127444946359, 0.11266018033215, 0.0980927625103667, 0.104916590166795, 0.0860999235367981, 0.106158253725478, 0.102570773477205, 0.06118841105676, 0.105804979598362, 0.0994196861729652, 0.115935876650863, -0.105466791174436, 0.101838113293825, 0.124950710904606, 0.0673073062385254, 0.0317940330935366, 0.0905508070559734, 0.109286343932539, 0.0856639697119484, 0.0975185539657231, 0.0994196861729652, 0.103479304149645, 0.0925951094262163, 0.0984737080806255, 0.0746900879658086, 0.104916590166795, 0.124950710904606, 0.13335753753099, 0.0971338624854, 0.0927970627378338, 0.0854451586907835, 0.123291662172605, 0.099795566659867, 0.075438259057915, 0.0790730002999382, 0.0953835529495729, 0.0852257858900505, 0.0905508070559734, 0.0816356880948164, 0.111322916452723, 0.085882223247457, 0.111658733889352, 0.102570773477205, 0.10721109183635, 0.10721109183635, 0.0731707992415186, 0.121147290839092, 0.101654123163447, 0.0919865892416036, 0.0890918891744519, 0.0897200446166977, 0.0700334141981384, 0.0773980324983756, 0.119121529360296, 0.124349981516283, 0.0925951094262163, 0.0988531856309209, 0.0871802709827442, 0.093600518723111, 0.095579614648734, 0.0761790825557647, 0.110817282218029, 0.0845642530341719, 0.129800688512867, 0.096359872295819, 0.0953835529495729, 0.113817398381304, 0.107036337986806, 0.121609982184857, 0.0951870874129862, 0.0901363829552483, 0.123594963843665, 0.116419281840054, 0.0773980324983756, 0.111154627274007, 0.113817398381304), .Dim = c(100L, 2L), .Dimnames = list(c(\'1\', \'2\', \'3\', \'4\', \'5\', \'6\', \'7\', \'8\', \'9\', \'10\', \'11\', \'12\', \'13\', \'14\', \'15\', \'16\', \'17\', \'18\', \'19\', \'20\', \'21\', \'22\', \'23\', \'24\', \'25\', \'26\', \'27\', \'28\', \'29\', \'30\', \'31\', \'32\', \'33\', \'34\', \'35\', \'36\', \'37\', \'38\', \'39\', \'40\', \'41\', \'42\', \'43\', \'44\', \'45\', \'46\', \'47\', \'48\', \'49\', \'50\', \'51\', \'52\', \'53\', \'54\', \'55\', \'56\', \'57\', \'58\', \'59\', \'60\', \'61\', \'62\', \'63\', \'64\', \'65\', \'66\', \'67\', \'68\', \'69\', \'70\', \'71\', \'72\', \'73\', \'74\', \'75\', \'76\', \'77\', \'78\', \'79\', \'80\', \'81\', \'82\', \'83\', \'84\', \'85\', \'86\', \'87\', \'88\', \'89\', \'90\', \'91\', \'92\', \'93\', \'94\', \'95\', \'96\', \'97\', \'98\', \'99\', \'100\'), c(\'(Intercept)\', \'x\'))), rank = 2L, qraux = c(1.00000155841949, 1.11047890709071), pivot = 1:2, tol = 1e-11), .Names = c(\'qr\', \'rank\', \'qraux\', \'pivot\', \'tol\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes27() {
        assertEval("argv <- list(structure(list(message = \'NAs produced\', call = quote(rnorm(2, numeric()))), .Names = c(\'message\', \'call\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes28() {
        assertEval("argv <- list(structure(c(7L, 7L, 7L, 7L, 7L, 7L, 7L, 7L, 7L, 7L, 7L, 7L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 5L, 8L, 8L, 8L, 8L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 9L, 10L, 10L, 10L, 10L, 10L, 10L, 10L, 10L, 10L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 6L, 6L, 6L, 6L, 6L, 6L, 6L, 6L, 6L, 6L, 6L, 6L, 6L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 27L, 27L, 21L, 21L, 21L, 21L, 21L, 21L, 21L, 21L, 21L, 21L, 21L, 21L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 20L, 22L, 22L, 22L, 22L, 22L, 22L, 22L, 22L, 22L, 22L, 22L, 22L, 22L, 23L, 23L, 23L, 23L, 23L, 23L, 23L, 23L, 23L, 23L, 23L, 23L, 23L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 18L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 26L, 25L, 25L, 25L, 25L, 25L, 25L, 25L, 25L, 25L, 25L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 24L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 11L, 12L, 12L, 12L, 12L, 12L, 12L, 12L, 12L, 12L, 12L, 17L, 17L, 17L, 13L, 13L, 13L, 13L, 13L, 13L, 13L, 13L, 13L, 13L, 13L, 13L, 16L, 16L, 16L, 16L, 16L, 16L, 16L, 16L, 15L, 15L, 15L, 15L, 15L, 15L, 15L, 15L, 15L, 14L, 14L, 14L, 14L, 14L, 14L, 14L, 14L, 14L), .Label = structure(c(\'9\', \'8\', \'7\', \'4\', \'2\', \'10\', \'1\', \'3\', \'5\', \'6\', \'21\', \'22\', \'24\', \'27\', \'26\', \'25\', \'23\', \'17\', \'11\', \'14\', \'13\', \'15\', \'16\', \'20\', \'19\', \'18\', \'12\'), .Names = c(\'Control1\', \'Control2\', \'Control3\', \'Control4\', \'Control5\', \'Control6\', \'Control7\', \'Control8\', \'Control9\', \'Control10\', \'High1\', \'High2\', \'High3\', \'High4\', \'High5\', \'High6\', \'High7\', \'Low1\', \'Low2\', \'Low3\', \'Low4\', \'Low5\', \'Low6\', \'Low7\', \'Low8\', \'Low9\', \'Low10\')), class = c(\'ordered\', \'factor\')));attributes(argv[[1]]);");
    }

    @Test
    public void testattributes29() {
        assertEval("argv <- list(structure(list(tau = c(-0.704193760852047, 0, 1.5847914530377, 2.07658624888165, 2.62779840842982, 3.16900609499152, 3.70430313207003), par.vals = structure(c(1.19410356771918, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 0.0145810141529953, 0.24263452560295, 0.470688037052905, 0.562956252821107, 0.683253495496408, 0.823187854524599, 0.98897386701965), .Dim = c(7L, 2L), .Dimnames = list(NULL, c(\'a\', \'b\')))), .Names = c(\'tau\', \'par.vals\')));attributes(argv[[1]]);");
    }
}
