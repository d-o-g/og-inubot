package com.inubot.private_.zulrah;

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
