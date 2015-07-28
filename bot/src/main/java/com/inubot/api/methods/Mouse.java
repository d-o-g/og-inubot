/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;

import java.awt.*;

public class Mouse {

    public static int getX() {
        return Inubot.getInstance().getCanvas().mouseX;
    }

    public static int getY() {
        return Inubot.getInstance().getCanvas().mouseY;
    }

    public static Point getLocation() {
        return new Point(getX(), getY());
    }

    public static void setLocation(int x, int y) {
        Inubot.getInstance().getCanvas().setMouseLocation(x, y);
    }

    public static int getIdleTime() {
        return Inubot.getInstance().getClient().getMouseIdleTime();
    }

    public static void click(boolean left) {
        Inubot.getInstance().getCanvas().clickMouse(left);
    }
}
