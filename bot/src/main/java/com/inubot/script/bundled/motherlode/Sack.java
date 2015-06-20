/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.methods.Varps;

public class Sack {

    public static final int VARP = 375;

    public static boolean hasOre() {
        return Varps.get(VARP) == 4;
    }

    public static boolean isEmpty() {
        return !hasOre();
    }

}