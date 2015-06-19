/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.RuneDream;
import com.inubot.client.natives.RSVarpBit;

/**
 * @author unsigned, Cameron
 * @since 21-04-2015
 */
public class Varps {

    public static int[] getAll() {
        int[] vars = RuneDream.getInstance().getClient().getVarps();
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
        return RuneDream.getInstance().getClient().getVarpBit(id);
    }
}
