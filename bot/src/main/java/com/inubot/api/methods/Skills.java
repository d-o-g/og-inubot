/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.Skill;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Skills {

    public static final int LEAST_SKILL = 1;
    public static final int MAX_SKILL = 99;
    public static final boolean[] ENABLED_SKILLS;
    public static final int[] XP_TABLE;

    static {
        ENABLED_SKILLS = new boolean[]{true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false};
        XP_TABLE = new int[MAX_SKILL];
        XP_TABLE[0] = 0;
        for (int i = LEAST_SKILL; i < MAX_SKILL; i++)
            XP_TABLE[i] = getExperienceAt(i);
    }

    public static int[] getExperiences() {
        return Inubot.getInstance().getClient().getExperiences();
    }

    /**
     * @return the 'real' levels
     */
    public static int[] getLevels() {
        return Inubot.getInstance().getClient().getRealLevels();
    }

    /**
     * @return gets the current level, accounts for potions and drained levels
     */
    public static int[] getCurrentLevels() {
        return Inubot.getInstance().getClient().getLevels();
    }

    /**
     * @param index
     * @return the current level
     */
    public static int getCurrentLevel(final int index) {
        return getCurrentLevels()[index];
    }

    /**
     * @param skill
     * @return the current level for skill
     */
    public static int getCurrentLevel(final Skill skill) {
        return getCurrentLevel(skill.getIndex());
    }

    /**
     * @param index
     * @return the 'real' level for skill
     */
    public static int getLevel(final int index) {
        return getLevels()[index];
    }

    /**
     * @param skill
     * @return the 'real' level for skill
     */
    public static int getLevel(final Skill skill) {
        return getLevel(skill.getIndex());
    }

    /**
     * @param index the skill index
     * @return experience for the skill
     */
    public static int getExperience(final int index) {
        return getExperiences()[index];
    }

    /**
     * @param skill the skill
     * @return the experience for the skill
     */
    public static int getExperience(final Skill skill) {
        return getExperience(skill.getIndex());
    }

    public static int getExperienceToNextLevel(final Skill skill) {
        int nextLvl = getLevel(skill) + 1;
        if (nextLvl > 99)
            nextLvl = 99;
        return getExperienceAt(nextLvl) - getExperience(skill);
    }

    public static int getExperienceAt(final int deslvl) {
        double t = 0;
        for (int lvl = 1; lvl < deslvl; lvl++)
            t += Math.floor(lvl + 300 * Math.pow(2, lvl / 7.0));
        return (int) Math.floor(t / 4);
    }

    public static int getMaxLevel() {
        return 99;
    }
}
