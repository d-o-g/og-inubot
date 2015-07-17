/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.event;

import com.inubot.api.oldschool.Skill;

import java.util.EventObject;

/**
 * @author Dogerina
 * @since 17-07-2015
 */
public class ExperienceEvent extends EventObject {

    private final int index;
    private final int old;
    private final int _new;

    public ExperienceEvent(int index, int old, int _new) {
        super(Skill.values()[index]);
        this.index = index;
        this.old = old;
        this._new = _new;
    }

    public int getSkillIndex() {
        return index;
    }

    /**
     * @return The {@link com.inubot.api.oldschool.Skill} which triggered this event
     */
    public Skill getSkill() {
        return Skill.values()[index];
    }

    /**
     * @return The experience after this event was triggered
     */
    public int getNew() {
        return _new;
    }

    /**
     * @return The experience before the event was triggered
     */
    public int getOld() {
        return old;
    }

    public int getGain() {
        return getNew() - getOld();
    }
}
