/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * @author unsigned
 * @since 21-05-2015
 */
public class Prayers {

    public static boolean isActive(final Prayer prayer) {
        return (Varps.get(83) & prayer.getBits()) != 0;
    }

    public static int getEngineVar() {
        return Varps.get(83);
    }

    public static int getTotalActive() {
        return Integer.bitCount(getEngineVar());
    }

    public static boolean isAnyActivated() {
        return getEngineVar() != 0;
    }

    public static boolean isAllDeactivated() {
        return !isAnyActivated();
    }

    public static int getPoints() {
        return Skills.getCurrentLevel(Skill.PRAYER);
    }

    public static Prayer[] getActive() {
        final int bit = Varps.get(83);
        final List<Prayer> active = new ArrayList<>();
        for (final Prayer prayer : Prayer.values()) {
            if ((bit & prayer.getBits()) == 0)
                continue;
            active.add(prayer);
        }
        return active.toArray(new Prayer[active.size()]);
    }

    public static void toggle(final boolean endState, final Prayer prayer) {
        final Widget widget = Interfaces.getWidget(271, prayer.getWidgetIndex());
        if (widget == null)
            return;
        boolean currState;
        if ((currState = isActive(prayer)) != endState)
            widget.processAction(!currState ? "Activate" : "Deactivate", prayer.toString());
    }

    public static void toggle(final Prayer prayer) {
        toggle(!isActive(prayer), prayer);
    }

    public static void flick(final Prayer prayer, final int delay) {
        final Widget widget = Interfaces.getWidget(271, prayer.getWidgetIndex());
        if (widget == null)
            return;
        widget.processAction(!isActive(prayer) ? "Activate" : "Deactivate", prayer.toString());
        Time.sleep(delay);
    }

    public static void flick(final Prayer prayer, final int delay, final int times) {
        for (int i = 0; i < times; i++)
            flick(prayer, delay);
    }
}
