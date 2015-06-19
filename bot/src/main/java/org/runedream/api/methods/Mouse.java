/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.RuneDream;

import java.awt.*;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Mouse {

    public static int getX() {
        return RuneDream.getInstance().getCanvas().mouseX;
    }

    public static int getY() {
        return RuneDream.getInstance().getCanvas().mouseY;
    }

    public static Point getLocation() {
        return new Point(getX(), getY());
    }

    public static void hop(int x, int y) {
        RuneDream.getInstance().getCanvas().setMouseLocation(x, y);
    }

    public static int getIdleTime() {
        return RuneDream.getInstance().getClient().getMouseIdleTime();
    }

    public static void click(boolean left) {
        RuneDream.getInstance().getCanvas().clickMouse(left);
    }
}
