/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.blastfurnace.mechanics;

import org.runedream.api.methods.Varps;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public class FurnaceMechanics {

    public static final int STATUS_VARP = 543;

    public static boolean isBroken() {
        return Varps.get(STATUS_VARP) > 0;
    }
}
