/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.skill.constraints;

import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;

public class SkillConstraint implements Constraint {

    private Skill skill;
    private int level;

    public SkillConstraint(Skill skill, int level) {
        this.skill = skill;
        this.level = level;
    }

    public SkillConstraint(int level) {
        this.level = level;
    }

    public SkillConstraint(Skill skill) {
        this(skill, 0);
    }

    public SkillConstraint() {

    }

    @Override
    public boolean isMet() {
        return Skills.getLevel(skill) >= level;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        this.level = level;
    }
}

