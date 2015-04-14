/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.library.graphics.core;

import com.oracle.truffle.r.library.graphics.core.geometry.Coordinates;

public interface GraphicsEngine {
    void registerGraphicsSystem(GraphicsSystem newGraphicsSystem) throws Exception;

    void unRegisterGraphicsSystem(GraphicsSystem graphicsSystem);

    void registerGraphicsDevice(GraphicsDevice newGraphicsDevice) throws Exception;

    void unRegisterGraphicsDevice(GraphicsDevice deviceToUnregister);

    int getGraphicsDevicesAmount();

    /**
     * @return true if there is only Null graphics device registered
     */
    boolean noGraphicsDevices();

    /**
     * Tries to install one if there is no current device.
     *
     * @return current {@link GraphicsDevice}
     */
    GraphicsDevice getCurrentGraphicsDevice();

    /**
     * @return {@link NullGraphicsDevice} if unable to find other
     */
    GraphicsDevice getGraphicsDeviceNextTo(GraphicsDevice graphicsDevice);

    /**
     * @return {@link NullGraphicsDevice} if unable to find other
     */
    GraphicsDevice getGraphicsDevicePrevTo(GraphicsDevice graphicsDevice);

    void setCurrentGraphicsDeviceMode(GraphicsDevice.Mode mode);

    void setCurrentGraphicsDeviceClipRect(double x1, double y1, double x2, double y2);

    void drawPolyline(Coordinates coordinates, DrawingParameters drawingParameters);
}
