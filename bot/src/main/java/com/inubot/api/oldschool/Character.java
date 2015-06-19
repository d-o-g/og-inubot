/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.Projection;
import com.inubot.client.natives.RSCharacter;
import com.inubot.client.natives.RSNpc;
import com.inubot.client.natives.RSPlayer;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.util.Identifiable;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public abstract class Character<T extends RSCharacter> extends Wrapper<T> implements Locatable, Processable, Identifiable {

    protected final int arrayIndex;

    public Character(T raw, int index) {
        super(raw);
        this.arrayIndex = index;
    }

    @Override
    public T getRaw() {
        return raw;
    }

    public int getStrictX() {
        return raw.getX();
    }

    public int getStrictY() {
        return raw.getY();
    }

    public int getRegionX() {
        return getStrictX() >> 7;
    }

    public int getRegionY() {
        return getStrictY() >> 7;
    }

    public int getX() {
        return Game.getRegionBaseX() + getRegionX();
    }

    public int getY() {
        return Game.getRegionBaseY() + getRegionY();
    }

    public int getHealth() {
        return raw.getHealth();
    }

    public int getMaxHealth() {
        return raw.getMaxHealth();
    }

    public int getHealthBarCycle() {
        return raw.getHealthBarCycle();
    }

    public int getTargetIndex() {
        return raw.getInteractingIndex();
    }

    public boolean isHealthBarVisible() {
        return getHealthBarCycle() > Game.getEngineCycle();
    }

    public Character<?> getTarget() {
        int index = getTargetIndex();
        if (index == -1)
            return null;
        if (index >= 32768)
            index -= 32768;
        if (index >= 32767)
            return null;
        RSPlayer[] players = Players.raw();
        RSNpc[] npcs = Npcs.raw();
        if (npcs == null || players == null)
            return null; //should never happen?
        RSNpc npc = index < npcs.length ? npcs[index] : null;
        RSPlayer player = index < players.length ? players[index] : null;
        if (index <= 32767 && npc != null)
            return new Npc(npc, index);
        return player != null ? new Player(player, index) : null;
    }

    public int getAnimation() {
        return raw.getAnimation();
    }

    @Override
    public Tile getLocation() {
        return new Tile(getX(), getY(), Game.getPlane());
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return distance(Players.getLocal());
    }

    public abstract String getName();

    public int getArrayIndex() {
        return arrayIndex;
    }

    public boolean isDying() {
        return isHealthBarVisible() && getHealth() == 0;
    }
}
