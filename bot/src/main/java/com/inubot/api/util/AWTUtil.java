/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.util;

import java.awt.*;

public class AWTUtil {

    public static final byte NORTH = 0x1;
    public static final byte SOUTH = 0x2;
    public static final byte EAST = 0x4;
    public static final byte WEST = 0x8;

    /**
     * Draws a string with a shadow behind it.
     *
     * @param g      The graphics to draw to.
     * @param string The text to draw.
     * @param x      The x location.
     * @param y      The y location.
     * @param fg     The foreground color.
     * @param bg     The background color.
     * @param flags
     */
    public static void drawShadowString(final Graphics g, final String string, final int x, final int y, final Color fg, final Color bg, final int flags) {
        g.setColor(bg);
        if ((flags & NORTH) == NORTH) {
            g.drawString(string, x, y - 1);
        }
        if ((flags & EAST) == EAST) {
            g.drawString(string, x + 1, y);
        }
        if ((flags & SOUTH) == SOUTH) {
            g.drawString(string, x, y + 1);
        }
        if ((flags & WEST) == WEST) {
            g.drawString(string, x - 1, y);
        }
        if ((flags & NORTH) == NORTH && (flags & EAST) == EAST) {
            g.drawString(string, x + 1, y - 1);
        }
        if ((flags & SOUTH) == SOUTH && (flags & EAST) == EAST) {
            g.drawString(string, x + 1, y + 1);
        }
        if ((flags & SOUTH) == SOUTH && (flags & WEST) == WEST) {
            g.drawString(string, x - 1, y + 1);
        }
        if ((flags & NORTH) == NORTH && (flags & WEST) == WEST) {
            g.drawString(string, x - 1, y - 1);
        }
        g.setColor(fg);
        g.drawString(string, x, y);
    }

    /**
     * Surrounds the text with a border.
     *
     * @param g      The graphics to draw to.
     * @param string The string to draw.
     * @param x      The x location.
     * @param y      The y location.
     * @param fg     The foreground color.
     * @param bg     The background color.
     */
    public static void drawBoldedString(final Graphics g, final String string, final int x, final int y, final Color fg, final Color bg) {
        drawShadowString(g, string, x, y, fg, bg, NORTH | SOUTH | EAST | WEST);
    }

    /**
     * Surrounds the text with a border.
     *
     * @param g      The graphics to draw to.
     * @param string The string to draw.
     * @param x      The x location.
     * @param y      The y location.
     * @param color  The color to draw the string as.
     */
    public static void drawBoldedString(final Graphics g, final String string, final int x, final int y, final Color color) {
        drawShadowString(g, string, x, y, color, Color.BLACK, NORTH | SOUTH | EAST | WEST);
    }
}