/*
 * Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 */

package com.sun.javafx.newt.windows;

import com.sun.javafx.newt.*;
import com.sun.opengl.impl.*;

public class WindowsWindow extends Window {
    private Screen screen;
    private long visualID;
    private boolean fullscreen, visible;
    private long window;
    // Default width and height -- will likely be re-set immediately by user
    private int width  = 100;
    private int height = 100;
    private int x=0;
    private int y=0;

    private static final String WINDOW_CLASS_NAME = "NewtWindow";
    static {
        NativeLibLoader.loadCore();

        if (!initIDs()) {
            throw new RuntimeException("Failed to initialize jmethodIDs");
        }
    }

    public WindowsWindow() {
    }

    public void initNative(Screen screen, long visualID) {
        this.screen = screen;
        this.visualID = visualID;
        long wndClass = getWindowClass();
        fullscreen=false;
        visible=false;
        window = CreateWindow(WINDOW_CLASS_NAME, getHInstance(), visualID, x, y, width, height);
        if (window == 0) {
            throw new RuntimeException("Error creating window");
        }
        screen.setHandle(0); // dummy
        screen.getDisplay().setHandle(0); // dummy
    }

    public Screen getScreen() {
        return screen;
    }

    public void setVisible(boolean visible) {
        if(this.visible!=visible) {
            this.visible=visible;
            setVisible0(window, visible);
        }
    }

    public void setSize(int width, int height) {
        setSize0(window, width, height);
    }

    public void setPosition(int x, int y) {
    }

    public boolean isVisible() {
        return visible;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean setFullscreen(boolean fullscreen) {
        if(this.fullscreen!=fullscreen) {
            this.fullscreen=fullscreen;
            return setFullScreen0(window, fullscreen);
        }
        return true;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public int getDisplayWidth() {
        return 640; // FIXME
    }

    public int getDisplayHeight() {
        return 480; // FIXME
    }

    public long getWindowHandle() {
        return window;
    }

    public void pumpMessages() {
        DispatchMessages(window);
    }

    //----------------------------------------------------------------------
    // Internals only
    //

    private static long windowClass;
    private static synchronized long getWindowClass() {
        if (windowClass == 0) {
            windowClass = RegisterWindowClass(WINDOW_CLASS_NAME, getHInstance());
            if (windowClass == 0) {
                throw new RuntimeException("Error while registering window class");
            }
        }
        return windowClass;
    }
    private static long hInstance;
    private static synchronized long getHInstance() {
        if (hInstance == 0) {
            // FIXME: will require modification once this is moved into its own DLL ("newt")
            hInstance = LoadLibraryW("jogl");
            if (hInstance == 0) {
                throw new RuntimeException("Error finding HINSTANCE for \"jogl\"");
            }
        }
        return hInstance;
    }

    private static native boolean initIDs();
    private static native long LoadLibraryW(String libraryName);
    private static native long RegisterWindowClass(String windowClassName, long hInstance);
    private        native long CreateWindow(String windowClassName, long hInstance, long visualID,
                                            int x, int y, int width, int height);
    private        native void setVisible0(long window, boolean visible);
    private static native void DispatchMessages(long window);
    private        native void setSize0(long window, int width, int height);
    private        native boolean setFullScreen0(long window, boolean fullscreen);

    private void keyDown(long key) {
    }

    private void keyUp(long key) {
    }

    private void sizeChanged(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
    }

    private void positionChanged(int newX, int newY) {
        x = newX;
        y = newY;
    }

    private void windowClosed() {
    }

    private void windowDestroyed() {
    }
}
