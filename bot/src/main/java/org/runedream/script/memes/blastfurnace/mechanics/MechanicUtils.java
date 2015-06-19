/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.blastfurnace.mechanics;

import org.runedream.client.natives.RSVarpBit;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public class MechanicUtils {

    public static void verifyVarpbit(String name, RSVarpBit chk, int expectedMax) {
        if (chk == null)
            return;
        int upper = chk.getRight() - chk.getLeft() + 1;
        upper = 1 << upper;
        if (upper - 1 != expectedMax)
            throw new RuntimeException("Bad Capacity(" + name + ")[expected=: " + expectedMax + ",got=" + upper + "]");
    }
}

