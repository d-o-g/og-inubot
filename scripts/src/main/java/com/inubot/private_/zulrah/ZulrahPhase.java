package com.inubot.private_.zulrah;

public class ZulrahPhase {

    private final ZulrahStyle style;
    private final ZulrahLocation location;

    public ZulrahPhase(ZulrahStyle style, ZulrahLocation location) {
        this.style = style;
        this.location = location;
    }

    public ZulrahStyle getStyle() {
        return style;
    }

    public ZulrahLocation getLocation() {
        return location;
    }
}
