/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.zulrah;

/**
 * @author Dogerina
 * @since 17-07-2015
 */
public enum ZulrahRotation {

    FIRST(new ZulrahPhase(ZulrahStyle.RANGE, ZulrahLocation.CENTER),
            new ZulrahPhase(ZulrahStyle.MELEE, ZulrahLocation.CENTER),
            new ZulrahPhase(ZulrahStyle.MAGE, ZulrahLocation.CENTER),
            new ZulrahPhase(ZulrahStyle.RANGE, ZulrahLocation.SOUTH),
            new ZulrahPhase(ZulrahStyle.MELEE, ZulrahLocation.CENTER),
            new ZulrahPhase(ZulrahStyle.MAGE, ZulrahLocation.WEST),
            new ZulrahPhase(ZulrahStyle.RANGE, ZulrahLocation.SOUTH),
            new ZulrahPhase(ZulrahStyle.MAGE, ZulrahLocation.SOUTH),
            new ZulrahPhase(ZulrahStyle.JAD, ZulrahLocation.WEST));

    private final ZulrahPhase[] phases;

    private ZulrahRotation(ZulrahPhase... phases) {
        this.phases = phases;
    }

    public ZulrahPhase[] getPhases() {
        return phases;
    }
}
