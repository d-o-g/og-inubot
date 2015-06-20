/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.methods.Game;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.action.UID;
import com.inubot.api.util.CacheLoader;
import com.inubot.client.natives.*;

import java.util.Objects;

public class Blockage {

    public static final String NAME = "Rockfall";

    public static volatile int atomic = 0;

    private final int id;
    private final Tile loc;

    public Blockage(Tile loc) {
        this.loc = Objects.requireNonNull(loc);
        this.id = atomic++;
    }

    public int getId() {
        return id;
    }

    public Tile getLocation() {
        return loc;
    }

    //Determines if the blockage can be validated to be blocked or unblocked.
    //Objects are not updated unless they are within the live-zone of the player:
    //(32x32 grid where the local player is the center).
    public boolean inRange() {
        return MotherloadMine.inLiveRange(loc);
    }

    private RSInteractableEntity get0() { //Gets the object at the specific location
        int rx = loc.getRegionX();
        int ry = loc.getRegionY();
        RSRegion r = Game.getClient().getRegion();
        RSTile t = r.getTiles()[0][rx][ry];
        if(t == null) return null;
        for(RSInteractableEntity e : t.getObjects()) {
            if(e == null) continue;
            if(e.getX() == rx && e.getY() == ry) {
                return e;
            }
        }
        return null;
    }

    public GameObject get() {
        RSInteractableEntity e = get0();
        if(e == null) return null;
        verify(loc,e.getId());
        return new GameObject(e);
    }

    static void verify(Tile pos, int uid) {
        uid = UID.getEntityId(uid);
        RSObjectDefinition def = CacheLoader.findObjectDefinition(uid);
        if(def != null) {
            if(!def.getName().equals(NAME)) {
                throw new InternalError("Non-Blockage @ " + pos + "(got=" + def.getName() + ")" );
            }
        }
    }

    public boolean isUnblocked() {
        if(!inRange()) return false;
        return get0() == null;
    }

    public boolean unblock() {
        GameObject rock = get();
        if(rock == null) return false;
        rock.processAction("Mine");
        //TODO await motion

        return false;
    }

    public String toString() {
        return "Blockage(" + loc + ")";
    }

}