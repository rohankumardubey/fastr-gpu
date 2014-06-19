/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.nodes.builtin.base;

import static com.oracle.truffle.r.runtime.RBuiltinKind.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.FileSystem;
import java.util.*;
import java.util.regex.*;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.builtin.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.model.*;

public class FileFunctions {
    private static final String INVALID_FILE_ARGUMENT = "invalid 'file' argument";

    @RBuiltin(name = "file.create", kind = INTERNAL)
    public abstract static class FileCreate extends RBuiltinNode {

        @Specialization
        public Object doFileCreate(RAbstractStringVector vec, byte showWarnings) {
            controlVisibility();
            byte[] status = new byte[vec.getLength()];
            for (int i = 0; i < status.length; i++) {
                String path = vec.getDataAt(i);
                if (path == RRuntime.STRING_NA) {
                    status[i] = RRuntime.LOGICAL_FALSE;
                } else {
                    boolean ok = true;
                    try {
                        new FileOutputStream(Utils.tildeExpand(path)).close();
                    } catch (IOException ex) {
                        ok = false;
                        if (showWarnings == RRuntime.LOGICAL_TRUE) {
                            RContext.getInstance().setEvalWarning("cannot create file '" + path + "'");
                        }
                    }
                    status[i] = RRuntime.asLogical(ok);
                }
            }
            return RDataFactory.createLogicalVector(status, RDataFactory.COMPLETE_VECTOR);
        }

        @Specialization(order = 100)
        public Object doFileCreate(@SuppressWarnings("unused") Object x, @SuppressWarnings("unused") Object y) {
            controlVisibility();
            CompilerDirectives.transferToInterpreter();
            throw RError.getGenericError(getEncapsulatingSourceSection(), INVALID_FILE_ARGUMENT);
        }
    }

    @RBuiltin(name = "file.info", kind = INTERNAL)
    public abstract static class FileInfo extends RBuiltinNode {
        private static final String[] NAMES = new String[]{"size", "isdir", "mode", "mtime", "ctime", "atime", "uid", "gid", "uname", "grname"};
        private static final RStringVector NAMES_VECTOR = RDataFactory.createStringVector(NAMES, RDataFactory.COMPLETE_VECTOR);

        @SuppressWarnings("unused")
        @Specialization
        public RList doFileInfo(RAbstractStringVector vec) {
            // TODO fill out all fields, create data frame, handle multiple files
            controlVisibility();
            int vecLength = vec.getLength();
            Object[] data = new Object[vecLength * NAMES.length];
            String[] rowNames = new String[vecLength];
            for (int i = 0; i < vecLength; i++) {
                int j = i * NAMES.length;
                for (int k = 0; k < NAMES.length; k++) {
                    data[j + k] = RRuntime.INT_NA;
                }
                String path = vec.getDataAt(i);
                File f = new File(Utils.tildeExpand(path));
                if (f.exists()) {
                    data[j] = (int) f.length();
                    data[j + 1] = RRuntime.asLogical(f.isDirectory());
                }
                rowNames[i] = path;
                // just 1 for now
                break;
            }
            RList list = RDataFactory.createList(data, new int[]{vecLength, NAMES.length});
            Object[] dimNamesData = new Object[]{RDataFactory.createStringVector(rowNames, vec.isComplete()), NAMES_VECTOR};
            list.setDimNames(RDataFactory.createList(dimNamesData));
            if (vecLength > 0) {
                list.setNames(NAMES_VECTOR);
            }
            return list;
        }
    }

    abstract static class FileLinkAdaptor extends RBuiltinNode {
        protected Object doFileLink(RAbstractStringVector vecFrom, RAbstractStringVector vecTo, boolean symbolic) {
            int lenFrom = vecFrom.getLength();
            int lenTo = vecTo.getLength();
            if (lenFrom < 1) {
                CompilerDirectives.transferToInterpreter();
                throw RError.getGenericError(getEncapsulatingSourceSection(), "nothing to link");
            }
            if (lenTo < 1) {
                return RDataFactory.createLogicalVector(0);
            }
            int len = lenFrom > lenTo ? lenFrom : lenTo;
            FileSystem fileSystem = FileSystems.getDefault();
            byte[] status = new byte[len];
            for (int i = 0; i < len; i++) {
                String from = vecFrom.getDataAt(i % lenFrom);
                String to = vecTo.getDataAt(i % lenTo);
                if (from == RRuntime.STRING_NA || to == RRuntime.STRING_NA) {
                    status[i] = RRuntime.LOGICAL_FALSE;
                } else {
                    Path fromPath = fileSystem.getPath(Utils.tildeExpand(from));
                    Path toPath = fileSystem.getPath(Utils.tildeExpand(to));
                    status[i] = RRuntime.LOGICAL_TRUE;
                    try {
                        if (symbolic) {
                            Files.createSymbolicLink(toPath, fromPath);
                        } else {
                            Files.createLink(toPath, fromPath);
                        }
                    } catch (UnsupportedOperationException | IOException ex) {
                        status[i] = RRuntime.LOGICAL_FALSE;
                        RContext.getInstance().setEvalWarning("  cannot link '" + from + "' to '" + to + "', reason " + ex.getMessage());
                    }
                }
            }
            return RDataFactory.createLogicalVector(status, RDataFactory.COMPLETE_VECTOR);
        }
    }

    @RBuiltin(name = "file.link", kind = INTERNAL)
    public abstract static class FileLink extends FileLinkAdaptor {
        @Specialization
        public Object doFileLink(RAbstractStringVector vecFrom, RAbstractStringVector vecTo) {
            controlVisibility();
            return doFileLink(vecFrom, vecTo, false);
        }

        @Specialization(order = 100)
        public Object doFileLink(@SuppressWarnings("unused") Object from, @SuppressWarnings("unused") Object to) {
            controlVisibility();
            CompilerDirectives.transferToInterpreter();
            throw RError.getGenericError(getEncapsulatingSourceSection(), INVALID_FILE_ARGUMENT);
        }
    }

    @RBuiltin(name = "file.symlink", kind = INTERNAL)
    public abstract static class FileSymLink extends FileLinkAdaptor {
        @Specialization
        public Object doFileSymLink(RAbstractStringVector vecFrom, RAbstractStringVector vecTo) {
            controlVisibility();
            return doFileLink(vecFrom, vecTo, true);
        }

        @Specialization(order = 100)
        public Object doFileSymLink(@SuppressWarnings("unused") Object from, @SuppressWarnings("unused") Object to) {
            controlVisibility();
            CompilerDirectives.transferToInterpreter();
            throw RError.getGenericError(getEncapsulatingSourceSection(), INVALID_FILE_ARGUMENT);
        }
    }

    @RBuiltin(name = "file.remove", kind = INTERNAL)
    public abstract static class FileRemove extends RBuiltinNode {

        @Specialization
        public Object doFileRemove(RAbstractStringVector vec) {
            controlVisibility();
            byte[] status = new byte[vec.getLength()];
            for (int i = 0; i < status.length; i++) {
                String path = vec.getDataAt(i);
                if (path == RRuntime.STRING_NA) {
                    status[i] = RRuntime.LOGICAL_FALSE;
                } else {
                    File f = new File(Utils.tildeExpand(path));
                    boolean ok = f.delete();
                    status[i] = RRuntime.asLogical(ok);
                    if (!ok) {
                        RContext.getInstance().setEvalWarning("  cannot remove file '" + path + "'");
                    }
                }
            }
            return RDataFactory.createLogicalVector(status, RDataFactory.COMPLETE_VECTOR);
        }

        @Specialization(order = 100)
        public Object doFileRemove(@SuppressWarnings("unused") Object x) {
            controlVisibility();
            CompilerDirectives.transferToInterpreter();
            throw RError.getGenericError(getEncapsulatingSourceSection(), INVALID_FILE_ARGUMENT);
        }
    }

    @RBuiltin(name = "file.rename", kind = INTERNAL)
    public abstract static class FileRename extends RBuiltinNode {
        @Specialization
        public Object doFileRename(RAbstractStringVector vecFrom, RAbstractStringVector vecTo) {
            controlVisibility();
            int len = vecFrom.getLength();
            if (len != vecTo.getLength()) {
                CompilerDirectives.transferToInterpreter();
                throw RError.getGenericError(getEncapsulatingSourceSection(), "'from' and 'to' are of different lengths");
            }
            byte[] status = new byte[len];
            for (int i = 0; i < len; i++) {
                String from = vecFrom.getDataAt(i);
                String to = vecTo.getDataAt(i);
                // GnuR's behavior regarding NA is quite inconsistent (error, warning, ignored)
                // we choose ignore
                if (from == RRuntime.STRING_NA || to == RRuntime.STRING_NA) {
                    status[i] = RRuntime.LOGICAL_FALSE;
                } else {
                    boolean ok = new File(Utils.tildeExpand(from)).renameTo(new File(Utils.tildeExpand(to)));
                    status[i] = RRuntime.asLogical(ok);
                    if (!ok) {
                        RContext.getInstance().setEvalWarning("  cannot rename file '" + from + "' to '" + to + "'");
                    }
                }
            }
            return RDataFactory.createLogicalVector(status, RDataFactory.COMPLETE_VECTOR);
        }

        @Specialization(order = 100)
        public Object doFileRename(@SuppressWarnings("unused") Object from, @SuppressWarnings("unused") Object to) {
            controlVisibility();
            CompilerDirectives.transferToInterpreter();
            throw RError.getGenericError(getEncapsulatingSourceSection(), INVALID_FILE_ARGUMENT);
        }
    }

    @RBuiltin(name = "file.exists", kind = INTERNAL)
    public abstract static class FileExists extends RBuiltinNode {

        @Specialization
        public Object doFileExists(RAbstractStringVector vec) {
            controlVisibility();
            byte[] status = new byte[vec.getLength()];
            for (int i = 0; i < status.length; i++) {
                String path = vec.getDataAt(i);
                if (path == RRuntime.STRING_NA) {
                    status[i] = RRuntime.LOGICAL_FALSE;
                } else {
                    File f = new File(Utils.tildeExpand(path));
                    // TODO R's notion of exists may not match Java - check
                    status[i] = f.exists() ? RRuntime.LOGICAL_TRUE : RRuntime.LOGICAL_FALSE;
                }
            }
            return RDataFactory.createLogicalVector(status, RDataFactory.COMPLETE_VECTOR);

        }

        @Specialization(order = 100)
        public Object doFileExists(@SuppressWarnings("unused") Object vec) {
            controlVisibility();
            CompilerDirectives.transferToInterpreter();
            throw RError.getGenericError(getEncapsulatingSourceSection(), INVALID_FILE_ARGUMENT);
        }
    }

    @RBuiltin(name = "list.files", kind = INTERNAL)
    public abstract static class ListFiles extends RBuiltinNode {

        // @formatter:off
        @SuppressWarnings("unused")
        @Specialization
        public RStringVector doListFiles(RAbstractStringVector vec, RAbstractStringVector patternVec, byte allFiles, byte fullNames, byte recursive,
                        byte ignoreCase, byte includeDirs, byte noDotDot) {
            // pattern in first element of vector, remaining elements are ignored (as per GnuR).
            String patternString = patternVec.getLength() == 0 ? "" : patternVec.getDataAt(0);
            final Pattern pattern = Pattern.compile(patternString);
            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                public boolean accept(Path file) throws IOException {
                    return pattern.matcher(file.getFileName().toString()).matches();
                }
            };

            // @formatter:on
            controlVisibility();
            // Curiously the result is not a vector of same length as the input,
            // as typical for R, but a single vector, which means duplicates may occur
            ArrayList<String> files = new ArrayList<>();
            for (int i = 0; i < vec.getLength(); i++) {
                String path = Utils.tildeExpand(vec.getDataAt(i));
                File dir = new File(path);
                if (!dir.exists()) {
                    continue;
                }
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir.toPath(), filter)) {
                    for (Path entry : stream) {
                        files.add(fullNames == RRuntime.LOGICAL_TRUE ? entry.toString() : entry.getFileName().toString());
                    }
                } catch (IOException ex) {
                    System.console();
                }
            }
            if (files.size() == 0) {
                return RDataFactory.createStringVectorFromScalar("");
            } else {
                String[] data = new String[files.size()];
                files.toArray(data);
                return RDataFactory.createStringVector(data, RDataFactory.COMPLETE_VECTOR);
            }
        }
    }

    // TODO make .Internal, when resolving .Platform in files.R is fixed
    // TODO handle the general case, which is similar to paste
    @RBuiltin(name = "file.path", kind = SUBSTITUTE)
    public abstract static class FilePath extends RBuiltinNode {
        private static final Object[] PARAMETER_NAMES = new Object[]{"...", "fsep"};

        @Override
        public Object[] getParameterNames() {
            return PARAMETER_NAMES;
        }

        @Override
        public RNode[] getParameterValues() {
            return new RNode[]{null, ConstantNode.create(File.separator)};
        }

        @Specialization(guards = "simpleArgs")
        public RStringVector doFilePath(Object[] args, String fsep) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < args.length; i++) {
                Object elem = args[i];
                String elemAsString;
                if (elem instanceof RStringVector) {
                    elemAsString = ((RStringVector) elem).getDataAt(0);
                } else if (elem instanceof Object[]) {
                    Object[] elemArray = (Object[]) elem;
                    for (int j = 0; j < elemArray.length - 1; j++) {
                        if (i != 0) {
                            sb.append(fsep);
                        }
                        sb.append(elemArray[j]);
                    }
                    elemAsString = (String) elemArray[elemArray.length - 1];
                } else {
                    elemAsString = (String) elem;
                }
                if (i != 0) {
                    sb.append(fsep);
                }
                sb.append(elemAsString);
            }
            return RDataFactory.createStringVectorFromScalar(sb.toString());
        }

        public static boolean simpleArgs(Object[] args, String fsep) {
            for (Object arg : args) {
                if (arg instanceof RStringVector) {
                    if (((RStringVector) arg).getLength() != 1) {
                        return false;
                    }
                } else {
                    if (arg instanceof String) {
                        continue;
                    } else if (arg instanceof Object[]) {
                        if (!simpleArgs((Object[]) arg, fsep)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }

    }

    private abstract static class XyzNameAdapter extends RBuiltinNode {
        abstract static class PathFunction {
            abstract String invoke(FileSystem fileSystem, String name);
        }

        protected RStringVector doXyzName(RAbstractStringVector vec, PathFunction fun) {
            FileSystem fileSystem = FileSystems.getDefault();
            boolean complete = RDataFactory.COMPLETE_VECTOR;
            String[] data = new String[vec.getLength()];
            for (int i = 0; i < data.length; i++) {
                String name = vec.getDataAt(i);
                if (name == RRuntime.STRING_NA) {
                    data[i] = name;
                    complete = RDataFactory.INCOMPLETE_VECTOR;
                } else if (name.length() == 0) {
                    data[i] = name;
                } else {
                    data[i] = fun.invoke(fileSystem, name);

                }

            }
            return RDataFactory.createStringVector(data, complete);

        }
    }

    @RBuiltin(name = "dirname", kind = INTERNAL)
    public abstract static class DirName extends XyzNameAdapter {
        private static class ParentPathFunction extends XyzNameAdapter.PathFunction {

            @Override
            String invoke(FileSystem fileSystem, String name) {
                Path path = fileSystem.getPath(Utils.tildeExpand(name));
                Path parent = path.getParent();
                return parent != null ? parent.toString() : name;
            }

        }

        private static final ParentPathFunction parentPathFunction = new ParentPathFunction();

        @Specialization
        public RStringVector doDirName(RAbstractStringVector vec) {
            return doXyzName(vec, parentPathFunction);
        }
    }

    @RBuiltin(name = "basename", kind = INTERNAL)
    public abstract static class BaseName extends XyzNameAdapter {
        private static class BasePathFunction extends XyzNameAdapter.PathFunction {

            @Override
            String invoke(FileSystem fileSystem, String name) {
                Path path = fileSystem.getPath(name);
                Path parent = path.getFileName();
                return parent != null ? parent.toString() : name;
            }

        }

        private static final BasePathFunction basePathFunction = new BasePathFunction();

        @Specialization
        public RStringVector doDirName(RAbstractStringVector vec) {
            return doXyzName(vec, basePathFunction);
        }
    }
}
