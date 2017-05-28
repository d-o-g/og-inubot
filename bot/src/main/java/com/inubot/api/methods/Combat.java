/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

public class Combat {

    private static final int AUTO_RETALIATE_VARP = 172;

    /**
     * @return <b>true</b> if auto retaliate is enabled, <b>false</b> otherwise
     */
    public static boolean isAutoRetaliating() {
        return !Varps.getBoolean(AUTO_RETALIATE_VARP);
    }

    /**
     * @param on <b>true</b> to set auto retaliating on, <b>false</b> otherwise
     */
    public static void setAutoRetaliating(boolean on) {
        if (isAutoRetaliating() != on) {
            Client.processAction(1, -1, 38862875, 57, "Auto retaliate", "", 50, 50);
        }
    }

    /**
     * @return The current combat style.
     * 0 for the top left button,
     * 1 for the top right button,
     * 3 for the bottom left button,
     * 2 for the bottom right button
     */
    public static int getStyle() { //0 = Punch, 1 = Kick, 3 = Block, 2 = the other one (bottom right)
        return Varps.get(43);
    }

    /**
     * @param style 0 to set the combat style to the style of the top left button,
     *              1 for the top right button,
     *              3 for the bottom left button,
     *              2 for the bottom right button
     */
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

    public static boolean isPoisoned() {
        return Varps.get(102) > 0;
    }
}
