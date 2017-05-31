/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.Inubot;
import com.inubot.api.methods.Game;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.Projection;
import com.inubot.client.natives.oldschool.*;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.util.Identifiable;

public abstract class PathingEntity<T extends RSPathingEntity> extends Wrapper<T> implements Locatable, Processable, Identifiable {

    protected final int arrayIndex;

    public PathingEntity(T raw, int index) {
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

    public RSNodeIterable<RSHealthBar> getHealthBars() {
        return raw.getHealthBars();
    }

    public int getHealthPercent() {
        for (RSHealthBar bar : getHealthBars()) {
            if (bar != null) {
                for (RSHitbar bar0 : bar.getHitsplats()) {
                    if (bar0 != null) {
                        return bar0.getHealthPercent();
                    }
                }
            }
        }
        return 0;
    }

    public boolean isHealthBarVisible() {
        for (int cycle : raw.getHitsplatCycles()) {
            if (cycle + 70 > Inubot.getInstance().getClient().getEngineCycle()) {
                return true;
            }
        }
        return false;
    }

    public int getTargetIndex() {
        return raw.getInteractingIndex();
    }

    public int getQueueSize() {
        return raw.getQueueSize();
    }

    public boolean isMoving() {
        return getQueueSize() != 0;
    }

    public PathingEntity<?> getTarget() {
        int index = getTargetIndex();
        if (index == -1)
            return null;
        if (index >= 32768)
            index -= 32768;
        if (index >= 32767)
            return null;
        RSPlayer[] players = Players.internal();
        RSNpc[] npcs = Npcs.internal();
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
        return isHealthBarVisible() && getHealthPercent() == 0;
    }

    public int getOrientation() {
        return raw.getOrientation();
    }
}
