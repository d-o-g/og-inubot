/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.Skill;

public class Health {

    /**
     * @return The current health of the local player
     */
    public static int getCurrent() {
        return Skills.getCurrentLevel(Skill.HITPOINTS);
    }

    /**
     * @return The maximum health of the local player (the hitpoints level)
     */
    public static int getMaximum() {
        return Skills.getLevel(Skill.HITPOINTS);
    }

    /**
     * @return current health percentage
     */
    public static int getPercentage() {
        return getCurrent() / getMaximum() * 100;
    }
}

