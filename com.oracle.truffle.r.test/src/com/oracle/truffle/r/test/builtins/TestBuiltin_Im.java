/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Copyright (c) 2012-2014, Purdue University
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates
 *
 * All rights reserved.
 */
package com.oracle.truffle.r.test.builtins;

import org.junit.*;

import com.oracle.truffle.r.test.*;

// Checkstyle: stop line length check
public class TestBuiltin_Im extends TestBase {

    @Test
    public void testIm1() {
        assertEval("argv <- list(c(0.117646597100126-0.573973479297987i, -0.740437474899139-0.482946826369552i, -0.333166449062945-0.753763230370951i, -0.256092192198247+0.707588353835588i, 0.522033838837248+0.102958580568997i, -0.651949901695459+0.059749937384601i, 0.235386572284857-0.70459646368007i, 0.077960849563711-0.71721816157401i, -0.563222209454641-0.518013590538404i, -0.068796124369349+0.97981641556181i, 0.244428915757284-0.330850507052219i, 0.451504053079215-0.090319593965852i, 0.04123292199294+0.214538826629216i, -0.422496832339625-0.738527704739573i, -0.451685375030484+0.126357395265016i, 0.375304016677864+0.436900190874168i, -0.674059300339186+0.084416799015191i, 0.739947510877334+0.418982404924464i, 0.509114684244823-0.086484623694157i, -0.535642839219739+0.289927561259502i, 0.629727249341749+0.707648659913726i, -0.262197489402468-0.502198718342861i, -0.333800277698424-0.317646103980588i, -0.422186107911717+0.317002735170286i, -0.616692335171505+0.068946145379939i, -0.136100485502624-0.487679764177213i, -0.68086000613138+0.047032323152903i, 0.296209908189768+0.585533462557103i, 0.43280012844045+0.136998748692477i, -0.680205941942733-0.256569497284745i, 0.787738847475178-0.375602871669773i, 0.76904224100091-0.561876363549783i, 0.332202578950118-0.343917234128459i, -0.983769553611346-0.088288289740869i, -0.046488672133508-0.622109071207677i, -0.280395335170247-0.088565112138884i, 0.379095891586975-0.727769566649926i, -0.372438756103829+0.630754115650567i, 0.976973386685621-0.113639895506141i, -0.150428076228347+0.615598727377677i, 0.762964492726935+0.377685645913312i, -0.7825325866026+0.365371705974346i, -0.792443423040311-0.029652870362208i, 0.265771060547393-0.106618612674382i, -0.076741350022367-0.422144111460857i, 0.120061986786934-0.623033085890884i, 0.636569674033849-0.133150964328944i, -0.145741981978782+0.529165019069452i, 0.516862044313609-0.388779864071743i, 0.368964527385086+0.089207223073295i, -0.215380507641693+0.845013004067436i, 0.065293033525315+0.962527968484271i, -0.034067253738464+0.684309429416465i, 0.328611964770906+0.215416587846774i, -0.583053183540166-0.668235480667835i, -0.782507286391418+0.318827979750013i, 0.037788399171079+0.174802700161256i, 0.310480749443137+0.074551177173735i, 0.436523478910183+0.428166764970505i, -0.458365332711106+0.02467498282614i, -0.271871452223431+0.426340387811162i, 0.590808184713385-0.344468770084509i, -0.349650387953555+0.386026568349676i, -0.865512862653374-0.265651625278222i, -0.236279568941097+0.118144511046681i, -0.197175894348552+0.134038645368463i, 0.866602113481861-0.172567291859327i, 0.031389337713892-0.607820631329035i, 0.754053785184521-0.219050378933476i, -0.499292017172261+0.168065383884658i, 0.151969488085021-0.827990593142535i, -0.266853748421854-0.866413193943766i, 0.071623062591495-0.867246686843546i, -0.788765741891382+0.508717463380604i, -0.228835546857432-0.349587041980114i, 0.500139791176978-0.016703152458872i, 0.15619107374708-0.485402548890295i, -0.369039310626083+0.398423724273751i, -0.611165916680421+0.020983586354237i, -0.399467692630093-0.421179989556223i, 0.411274074028001+0.133781691724871i, 0.573364366690245+0.328833257005489i, -0.265145056696353-0.938538703606894i, 0.387209171815106+0.750271083217101i, -0.41433994791886-0.437159533180399i, -0.476246894615578+0.331179172958982i, -0.168543113030619+0.43048451175239i, -0.594617267459511+0.211980433372292i, 0.388005062566602-0.290649953587954i, -0.013004326537709-0.490434895455784i, 0.069845221019376-0.762134635168809i, 0.243687429599092+0.756774763795962i, 0.27384734040072+0.383667165938905i, -0.51606383094478-0.601506708006782i, -0.894951082455532+0.317442909372288i, 0.5073401683933-0.213001485168032i, -0.441163216905286-0.105671334003774i, -0.343169835663372+0.597359384628839i, -0.283179001991236-0.385834501657171i, -0.517794900198098-0.36732932802092i));Im(argv[[1]]);");
    }

    @Test
    public void testIm2() {
        assertEval(Ignored.Unknown, "argv <- list(structure(numeric(0), .Dim = c(0L, 0L)));Im(argv[[1]]);");
    }

    @Test
    public void testIm3() {
        assertEval(Ignored.Unknown, "argv <- list(FALSE);Im(argv[[1]]);");
    }

    @Test
    public void testIm4() {
        assertEval("argv <- list(c(0+0i, 0.01-0i, 0.02-0i, 0.03-0i, 0.04-0i, 0.05-0i, 0.06-0i, 0.07-0i, 0.08-0i, 0.09-0i, 0.1-0i, 0.11-0i, 0.12-0i, 0.13-0i, 0.14-0i, 0.15-0i, 0.16-0i, 0.17-0i, 0.18-0i, 0.19-0i, 0.2-0i, 0.21-0i, 0.22-0i, 0.23-0i, 0.24-0i, 0.25-0i, 0.26-0i, 0.27-0i, 0.28-0i, 0.29-0i, 0.3-0i, 0.31-0i, 0.32-0i, 0.33-0i, 0.34-0i, 0.35-0i, 0.36-0i, 0.37-0i, 0.38-0i, 0.39-0i, 0.4-0i, 0.41-0i, 0.42-0i, 0.43-0i, 0.44-0i, 0.45-0i, 0.46-0i, 0.47-0i, 0.48-0i, 0.49-0i, 0.5-0i, 0.51-0i, 0.52-0i, 0.53-0i, 0.54-0i, 0.55-0i, 0.56-0i, 0.57-0i, 0.58-0i, 0.59-0i, 0.6-0i, 0.61-0i, 0.62-0i, 0.63-0i, 0.64-0i, 0.65-0i, 0.66-0i, 0.67-0i, 0.68-0i, 0.69-0i, 0.7-0i, 0.71-0i, 0.72-0i, 0.73-0i, 0.74-0i, 0.75-0i, 0.76-0i, 0.77-0i, 0.78-0i, 0.79-0i, 0.8-0i, 0.81-0i, 0.82-0i, 0.83-0i, 0.84-0i, 0.85-0i, 0.86-0i, 0.87-0i, 0.88-0i, 0.89-0i, 0.9-0i, 0.91-0i, 0.92-0i, 0.93-0i, 0.94-0i, 0.95-0i, 0.96-0i, 0.97-0i, 0.98-0i, 0.99-0i, 1-0i));Im(argv[[1]]);");
    }

    @Test
    public void testIm5() {
        assertEval(Ignored.Unknown,
                        "argv <- list(structure(c(3+2i, 3+2i, NA, 3+2i, 3+2i, 3+2i, 3+2i, 3+2i, 4-5i, 3-5i, NA, NA, 2-5i, 3-5i, 4-5i, 5-5i), .Dim = c(8L, 2L), .Dimnames = list(NULL, c('x1', 'x2'))));Im(argv[[1]]);");
    }

    @Test
    public void testIm6() {
        assertEval(Ignored.Unknown,
                        "argv <- list(c(0.923879532511287+0.38268343236509i, 0.707106781186548+0.707106781186547i, 0.38268343236509+0.923879532511287i, 0+1i, -0.38268343236509+0.923879532511287i, -0.707106781186547+0.707106781186548i, -0.923879532511287+0.38268343236509i, -1+0i, -0.923879532511287-0.38268343236509i, -0.707106781186548-0.707106781186547i, -0.38268343236509-0.923879532511287i, 0-1i, 0.38268343236509-0.923879532511287i, 0.707106781186547-0.707106781186548i, 0.923879532511287-0.38268343236509i, 1-0i));Im(argv[[1]]);");
    }

    @Test
    public void testIm() {
        assertEval("{ Im(1+1i) }");
        assertEval("{ Im(1) }");
        assertEval("{ Im(c(1+1i,2-2i)) }");
        assertEval("{ Im(c(1,2)) }");
        assertEval("{ Im(as.double(NA)) }");
        assertEval("{ Im(c(1,NA,2)) }");
        assertEval("{ Im(NA+2i) }");

        assertEval(Ignored.Unknown, "{ x <- 1:2 ; attr(x,\"my\") <- 2 ; Im(x) }");
        assertEval(Ignored.Unknown, "{ x <- c(1+2i,3-4i) ; attr(x,\"my\") <- 2 ; Im(x) }");
    }
}
