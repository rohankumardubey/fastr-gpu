/*
 * Copyright (c) 2015, 2015, Oracle and/or its affiliates. All rights reserved.
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
#ifndef RFFIUTILS_H
#define RFFIUTILS_H

#include <jni.h>
#include <Rinternals.h>

JNIEnv *getEnv();
void setEnv(JNIEnv *env);

jclass checkFindClass(JNIEnv *env, const char *name);
jmethodID checkGetMethodID(JNIEnv *env, jclass klass, const char *name, const char *sig, int isStatic);
extern jmethodID createSymbolMethodID;

void unimplemented(char *msg);
void fatalError(char *msg);
void validate(SEXP x);
SEXP mkGlobalRef(JNIEnv *env, SEXP);
SEXP mkNamedGlobalRef(JNIEnv *env, int index, SEXP);

void init_variables(JNIEnv *env, jobjectArray initialValues);
void init_register(JNIEnv *env);
void init_rf_functions(JNIEnv *env);
void init_externalptr(JNIEnv *env);
void init_typecoerce(JNIEnv *env);
void init_attrib(JNIEnv *env);
void init_misc(JNIEnv *env);
void init_vectoraccess(JNIEnv *env);
void init_listaccess(JNIEnv *env);
void init_utils(JNIEnv *env);

extern jclass RDataFactoryClass;
extern jclass CallRFFIHelperClass;

#endif /* RFFIUTILS_H */
