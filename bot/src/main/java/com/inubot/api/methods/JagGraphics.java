/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.client.natives.oldschool.RSFont;

public class JagGraphics {

    //TODO hook more string plotting methods, and other drawing shit like drawRectangle, drawLine etc

    public static RSFont getFont_p12full() {
        return Inubot.getInstance().getClient().getFont_p12full();
    }

    public static void drawRect(int x, int y, int w, int h, int rgb) {
        Inubot.getInstance().getClient().drawRectangle(x, y, w, h, rgb);
    }

    /**
     * @param string
     * @param x
     * @param y
     * Draws a string using the clients font_p12full
     */
    public static void drawString(String string, int x, int y) {
        RSFont font = getFont_p12full();
        if (font == null)
            throw new IllegalStateException("null font_p12full?");
        font.drawString(string, x, y);
    }
}
