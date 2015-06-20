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

public class Vein {

    public static final String DEPLETED_VEIN = "Depleted vein";
    public static final String ORE_VEIN = "Ore vein";

    public static volatile int atomic = 0;

    final int id;
    final Tile obj;
    final Tile dest;

    public Vein(Tile obj_loc, Tile dest) {
        this.id = atomic++;
        this.obj = obj_loc;
        this.dest = dest;
    }

    public int getId() {
        return id;
    }

    public Tile getObjLoc() {
        return obj;
    }

    public Tile getDestination() {
        return dest;
    }

    public boolean inRange() {
        return MotherloadMine.inLiveRange(obj);
    }

    private RSBoundary get0() {
        if(!inRange()) return null;
        RSRegion r = Game.getClient().getRegion();
        RSTile t = r.getTiles()[0][obj.getRegionX()][obj.getRegionY()];
        if(t == null) return null;
        return t.getBoundary();
    }

    public GameObject get() {
        RSBoundary b = get0();
        if(b == null) return null;
        return new GameObject(b);
    }

    public boolean hasOre() {
        RSBoundary b = get0();
        if(b == null) return false;
        RSObjectDefinition def = CacheLoader.findObjectDefinition(UID.getEntityId(b.getId()));
        String name = def.getName();

        boolean o = false;
        if( ! ( (o=name.equals(ORE_VEIN)) || name.equals(DEPLETED_VEIN) ) )
            throw new InternalError("Object @ " + obj + " is not a vein: " + name);
        return o;
    }

    public boolean isDepleted() {
        return !hasOre();
    }

    public void mine() {
        GameObject v = get();
        if(v == null) return;
        v.processAction("Mine");
        //TODO await animation / walking
    }

}