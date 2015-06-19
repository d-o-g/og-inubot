/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes.blastfurnace.dispenser;

import com.inubot.api.methods.Varps;
import com.inubot.client.natives.RSVarpBit;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public enum DispenserState {

    EMPTY, MOLDING, DONE_HOT, DONE_COLD;

    public static final RSVarpBit DISPENSER_VAR;

    static { //TODO bit range verify
        DISPENSER_VAR = Varps.getBit(936);
        if (DISPENSER_VAR == null)
            throw new InternalError("Dispenser varpbit == null");
    }

    public static boolean is(final DispenserState state) {
        return getCurrent() == state;
    }

    public static DispenserState getCurrent() {
        for (final DispenserState state : values()) {
            if (state.ordinal() == DISPENSER_VAR.getValue())
                return state;
        }
        throw new InternalError("UNKNOWN DISPENSER STATE: PLEASE FIND OUT WHAT IT IS AND ADD TO DISPENSERSTATE ENUM");
    }

    public static boolean isDone() {
        return getCurrent().ordinal() >= DONE_HOT.ordinal();
    }
}
