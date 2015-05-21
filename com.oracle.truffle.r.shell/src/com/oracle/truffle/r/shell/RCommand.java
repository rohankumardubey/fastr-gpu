/*
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.shell;

import static com.oracle.truffle.r.runtime.RCmdOptions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import jline.console.*;

import com.oracle.truffle.api.source.*;
import com.oracle.truffle.r.engine.*;
import com.oracle.truffle.r.nodes.builtin.base.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.RContext.ConsoleHandler;
import com.oracle.truffle.r.runtime.RContext.Engine;
import com.oracle.truffle.r.runtime.data.*;

/**
 * Emulates the (Gnu)R command as precisely as possible.
 */
public class RCommand {

    // CheckStyle: stop system..print check

    public static void main(String[] args) {
        try {
            RCmdOptionsParser.Result result = RCmdOptionsParser.parseArguments(RCmdOptions.Client.R, args);
            if (HELP.getValue()) {
                RCmdOptionsParser.printHelp(RCmdOptions.Client.R, 0);
            } else if (VERSION.getValue()) {
                printVersionAndExit();
            } else if (RHOME.getValue()) {
                printRHomeAndExit();
            }
            subMain(result.args);
        } catch (Utils.DebugExitException ex) {
            // running under in-process debugger, just return
            return;
        }
    }

    /**
     * Entry point for {@link RscriptCommand} avoiding re-parsing.
     */
    public static void subMain(String[] args) {

        if (SLAVE.getValue()) {
            QUIET.setValue(true);
            NO_SAVE.setValue(true);
        }

        if (VANILLA.getValue()) {
            NO_SAVE.setValue(true);
            NO_ENVIRON.setValue(true);
            NO_INIT_FILE.setValue(true);
            NO_RESTORE.setValue(true);
        }

        String fileArg = FILE.getValue();
        if (fileArg != null) {
            if (EXPR.getValue() != null) {
                Utils.fatalError("cannot use -e with -f or --file");
            }
            if (!SAVE.getValue()) {
                NO_SAVE.setValue(true);
            }
            if (fileArg.equals("-")) {
                // means stdin, but still implies NO_SAVE
                fileArg = null;
            }
        }

        RContextFactory.initialize();

        if (!(QUIET.getValue() || SILENT.getValue())) {
            System.out.println(RRuntime.WELCOME_MESSAGE);
        }
        /*
         * Whether the input is from stdin, a file (-f), or an expression on the command line (-e)
         * it goes through the console. N.B. -f and -e can't be used together and this is already
         * checked.
         */
        ConsoleHandler consoleHandler;
        InputStream consoleInput = System.in;
        OutputStream consoleOutput = System.out;
        String filePath = null;
        if (fileArg != null) {
            List<String> lines;
            try {
                File file = new File(fileArg);
                lines = Files.readAllLines(file.toPath());
                filePath = file.getCanonicalPath();
            } catch (IOException e) {
                throw Utils.fatalError("cannot open file '" + fileArg + "': " + e.getMessage());
            }
            consoleHandler = new StringConsoleHandler(lines, System.out);
        } else if (EXPR.getValue() != null) {
            List<String> exprs = EXPR.getValue();
            if (!SAVE.getValue()) {
                NO_SAVE.setValue(true);
            }
            consoleHandler = new StringConsoleHandler(exprs, System.out);
        } else {
            /*
             * GnuR behavior differs from the manual entry for {@code interactive} in that {@code
             * --interactive} never applies to {@code -e/-f}, only to console input that has been
             * redirected from a pipe/file etc.
             */
            Console sysConsole = System.console();
            ConsoleReader consoleReader;
            try {
                consoleReader = new ConsoleReader(consoleInput, consoleOutput);
                consoleReader.setHandleUserInterrupt(true);
                consoleReader.setExpandEvents(false);
            } catch (IOException ex) {
                throw Utils.fail("unexpected error opening console reader");
            }
            boolean isInteractive = INTERACTIVE.getValue() || sysConsole != null;
            if (!isInteractive && !SAVE.getValue() && !NO_SAVE.getValue() && !VANILLA.getValue()) {
                throw Utils.fatalError("you must specify '--save', '--no-save' or '--vanilla'");
            }
            // long start = System.currentTimeMillis();
            consoleHandler = new JLineConsoleHandler(isInteractive, consoleReader);
        }
        // never returns
        readEvalPrint(consoleHandler, args, filePath);
        assert false;
    }

    private static void printVersionAndExit() {
        System.out.print("FastR version ");
        System.out.println(RVersionNumber.FULL);
        System.out.println(RRuntime.LICENSE);
        Utils.exit(0);
    }

    private static void printRHomeAndExit() {
        System.out.println(REnvVars.rHome());
        throw Utils.exit(0);
    }

    private static final Source QUIT_EOF = Source.fromText("quit(\"default\", 0L, TRUE)", "<quit_file>");

    /**
     * The read-eval-print loop, which can take input from a console, command line expression or a
     * file. There are two ways the repl can terminate:
     * <ol>
     * <li>A {@code quit} command is executed successfully, which case the system exits from the
     * {@link Quit} {@code .Internal} .</li>
     * <li>EOF on the input.</li>
     * </ol>
     * In case 2, we must implicitly execute a {@code quit("default, 0L, TRUE} command before
     * exiting. So,in either case, we never return.
     */
    private static void readEvalPrint(ConsoleHandler consoleHandler, String[] commandArgs, String filePath) {
        String inputDescription = filePath == null ? "<shell_input>" : filePath;
        Source source = Source.fromNamedAppendableText(inputDescription);
        RContext context = RContextFactory.createInitial(commandArgs, consoleHandler).activate();
        try {
            // console.println("initialize time: " + (System.currentTimeMillis() - start));
            for (;;) {
                boolean doEcho = doEcho();
                consoleHandler.setPrompt(doEcho ? "> " : null);
                String input = consoleHandler.readLine();
                if (input == null) {
                    throw new EOFException();
                }
                // Start index of the new input
                int startLength = source.getLength();
                // Append the input as is
                source.appendCode(input);
                input = input.trim();
                if (input.equals("") || input.charAt(0) == '#') {
                    // nothing to parse
                    continue;
                }

                try {
                    String continuePrompt = getContinuePrompt();
                    Source subSource = Source.subSource(source, startLength);
                    while (context.getThisEngine().parseAndEval(subSource, true, true) == Engine.INCOMPLETE_SOURCE) {
                        consoleHandler.setPrompt(doEcho ? continuePrompt : null);
                        String additionalInput = consoleHandler.readLine();
                        if (additionalInput == null) {
                            throw new EOFException();
                        }
                        source.appendCode(additionalInput);
                        subSource = Source.subSource(source, startLength);
                    }
                } catch (BrowserQuitException ex) {
                    // Q in browser, which continues the repl
                }
            }
        } catch (BrowserQuitException e) {
            // can happen if user profile invokes browser
        } catch (UserInterruptException e) {
            // interrupted (how?)
        } catch (EOFException ex) {
            context.getThisEngine().parseAndEval(QUIT_EOF, false, false);
        } finally {
            context.destroy();
        }
    }

    private static boolean doEcho() {
        if (SLAVE.getValue()) {
            return false;
        }
        RLogicalVector echo = (RLogicalVector) RRuntime.asAbstractVector(RContext.getROptionsState().getValue("echo"));
        return RRuntime.fromLogical(echo.getDataAt(0));
    }

    private static String getContinuePrompt() {
        RStringVector continuePrompt = (RStringVector) RRuntime.asAbstractVector(RContext.getROptionsState().getValue("continue"));
        return continuePrompt.getDataAt(0);
    }

}
