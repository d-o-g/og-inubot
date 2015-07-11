/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogerina.slayer;

import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;

/**
 * @author Dogerina
 * @since 07-07-2015
 */
public final class SkillConstraint implements Constraint {

    private final int level;
    private final Skill skill;

    public SkillConstraint(int level, Skill skill) {
        this.level = level;
        this.skill = skill;
    }

    public int getLevel() {
        return level;
    }

    public Skill getSkill() {
        return skill;
    }

    @Override
    public boolean isMet() {
        return Skills.getLevel(skill) >= level;
    }
}
