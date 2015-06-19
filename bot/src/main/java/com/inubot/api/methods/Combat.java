/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

/**
 * @author unsigned
 * @since 01-05-2015
 */
public class Combat {

    private static final int AUTO_RETALIATE_VARP = 172;

    public static boolean isAutoRetaliating() {
        return !Varps.getBoolean(AUTO_RETALIATE_VARP);
    }

    public static void setAutoRetaliating(boolean on) {
        if (isAutoRetaliating() != on) {
            Client.processAction(1, -1, 38862875, 57, "Auto retaliate", "", 50, 50);
        }
    }

    public static int getStyle() { //0 = Punch, 1 = Kick, 3 = Block, 2 = the other one (bottom right)
        return Varps.get(43);
    }

    //0 = top left button, 1 = top right, 3 = bottom left, 2 = bottom right
    public static void setStyle(int style) {
        if (getStyle() == style)
            return;
        int hash = 38862851;
        switch (style) {
            case 1:
                hash += 4;
                break;
            case 2:
                hash += 12;
                break;
            case 3:
                hash += 8;
                break;
        }
        Client.processAction(1, -1, hash, 57, "", "", 50, 50);
    }
}
