/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods;

import com.inubot.Inubot;
import com.inubot.client.natives.RSVarpBit;

public class Varps {

    public static int[] getAll() {
        int[] vars = Inubot.getInstance().getClient().getVarps();
        return vars == null ? null : vars;
    }

    public static int get(int index) {
        int[] vars = getAll();
        if (vars.length == 0)
            return -1;
        if (index >= vars.length || index < 0)
            throw new IllegalArgumentException("bad_index");
        return vars[index];
    }

    public static boolean getBoolean(int index) {
        return get(index) == 1;
    }

    public static RSVarpBit getBit(int id) {
        return Inubot.getInstance().getClient().getVarpBit(id);
    }
}
