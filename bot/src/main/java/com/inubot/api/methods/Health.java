/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.Skill;

public class Health {

    public static int getCycle() {
        return Players.getLocal().getHealthBarCycle();
    }

    public static int getCurrent() {
        return Skills.getCurrentLevel(Skill.HITPOINTS);
    }

    public static int getMaximum() {
        return Skills.getLevel(Skill.HITPOINTS);
    }

    public static int getPercentage() {
        return getCurrent() / getMaximum() * 100;
    }
}

