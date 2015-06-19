/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.RuneDream;
import org.runedream.client.natives.RSFont;

/**
 * @author unsigned
 * @since 09-06-2015
 */
public class JagGraphics {

    //TODO hook more string plotting methods, and other drawing shit like drawRectangle, drawLine etc

    public static RSFont getFont_p12full() {
        return RuneDream.getInstance().getClient().getFont_p12full();
    }

    public static void drawRect(int x, int y, int w, int h, int rgb) {
        RuneDream.getInstance().getClient().drawRectangle(x, y, w, h, rgb);
    }
}
