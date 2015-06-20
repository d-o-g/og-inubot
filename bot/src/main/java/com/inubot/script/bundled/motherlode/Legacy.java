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

import java.lang.reflect.Field;
import java.util.HashSet;

public class Legacy {

    HashSet<Tile> veins = new HashSet<>();

    public void getVeins() {

        RSClient client = Game.getClient();
        RSRegion region = client.getRegion();
        RSTile[][] floor = region.getTiles()[client.getPlane()];

        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                //Search for intractable boundary objects
                RSTile t = floor[x][y];
                if (t == null) continue;
                RSBoundary b = t.getBoundary();
                if (b == null) continue;
                if (b.getId() <= 0) continue; //Non intractable
                if(-b.getPlane() <= 500) continue; //it's on the ground floor
                int id = UID.getEntityId(b.getId());
                RSObjectDefinition def = CacheLoader.findObjectDefinition(id);
                if (def == null) continue;
                String name = def.getName();
                if (name.equals(Vein.ORE_VEIN) || name.equals(Vein.DEPLETED_VEIN)) {
                    Tile loc = translate(b);
                    if(!veins.contains(loc)) {
                        System.out.println(new GameObject(b).getLocation() + ":" + loc);
                        veins.add(loc);
                    }
                }
            }
        }



    }




    int[] DX = new int[] { 0, 1, 0, -1 };
    int[] DY = new int[] { 1, 0, -1, 0 };

    public GameObject getVein(int px, int py) {

        RSClient client = Game.getClient();
        RSRegion region = client.getRegion();
        RSTile[][] floor = region.getTiles()[client.getPlane()];

        double dist = Double.MAX_VALUE;
        RSGameObject best = null;

        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                //Search for intractable boundary objects
                RSTile t = floor[x][y];
                if (t == null) continue;
                RSBoundary b = t.getBoundary();
                if (b == null) continue;
                if (b.getId() <= 0) continue; //Non intractable
                int id = UID.getEntityId(b.getId());
                RSObjectDefinition def = CacheLoader.findObjectDefinition(id);
                if (def == null) continue;
                String name = def.getName();
                if (name.equals(Vein.ORE_VEIN)) {
                    if(-b.getPlane() > 500) continue; //It's on the ground floor
                    double d = Math.hypot(px-(b.getX() >> 7),py-(b.getY() >> 7));
                    if(d < dist) {
                        dist = d;
                        best = b;
                    }
                }
            }
        }

        if(best == null) return null;

        return new GameObject(best);

    }

    Tile translate(RSBoundary b) {
        int rot = getConfig(b) >> 6 & 0x3;
        rot = (rot+3)%4;
        Tile loc = new Tile(
                Game.getRegionBaseX() + (b.getX() >> 7),
                Game.getRegionBaseY() + (b.getY() >> 7));
        return loc.derive(DX[rot],DY[rot]);
    }

    int getConfig(RSBoundary b) {
        try {
            RSClient client = Game.getClient();
            Field cfg = client.getClass().getClassLoader().loadClass("cv").getDeclaredField("f");
            cfg.setAccessible(true);
            return cfg.getInt(b) * 896221583;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}