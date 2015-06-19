/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.RuneDream;
import org.runedream.api.oldschool.GameObject;
import org.runedream.api.oldschool.GameObject.Landmark;
import org.runedream.api.oldschool.Locatable;
import org.runedream.api.oldschool.collection.GameObjectPool;
import org.runedream.api.util.filter.Filter;
import org.runedream.client.natives.*;

import java.util.*;

/**
 * @author unsigned
 * @since 23-04-2015
 */
public class GameObjects {

    private static GameObjectPool pool;

    public static GameObjectPool getPool(boolean cached) {
        return cached && pool != null ? pool : (pool = new GameObjectPool(getLoaded()));
    }

    public static GameObjectPool getPool() {
        return pool;
    }

    public static GameObject[] getLoadedAt(int rx, int ry, int z) {
        if (rx > 104 || rx < 0 || ry > 104 || ry < 0 || z < 0 || z > 3)
            return new GameObject[0];
        List<GameObject> objs = new ArrayList<>();
        RSTile[][][] tiles = RuneDream.getInstance().getClient().getRegion().getTiles();
        if (tiles == null)
            return new GameObject[0];
        RSTile tile = tiles[z][rx][ry];
        if (tile == null)
            return new GameObject[0];
        RSFloorDecoration deco = tile.getDecoration();
        RSBoundaryDecoration boundaryDeco = tile.getBoundaryDecoration();
        RSBoundary boundary = tile.getBoundary();
        if (deco != null)
            objs.add(new GameObject(deco));
        if (boundary != null)
            objs.add(new GameObject(boundary));
        if (boundaryDeco != null)
            objs.add(new GameObject(boundaryDeco));
        RSInteractableEntity[] entities = tile.getObjects();
        if (entities != null && entities.length > 0) {
            for (RSInteractableEntity entity : entities) {
                if (entity == null)
                    continue;
                objs.add(new GameObject(entity));
            }
        }
        return objs.toArray(new GameObject[objs.size()]);
    }

    public static GameObject[] getLoaded(Filter<GameObject> filter) {
        List<GameObject> objs = new ArrayList<>();
        int z = Game.getPlane();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                for (GameObject obj : getLoadedAt(x, y, z)) {
                    if (!filter.accept(obj))
                        continue;
                    objs.add(obj);
                }
            }
        }
        return objs.toArray(new GameObject[objs.size()]);
    }

    public static GameObject[] getLoaded() {
        return getLoaded(Filter.always());
    }

    public static GameObject[] getFirstWithin(int dist, Filter<GameObject> filter) {
        List<GameObject> objs = new ArrayList<>();
        for (GameObject obj : getLoaded()) {
            if (obj == null || dist != -1 && obj.distance() > dist || !filter.accept(obj))
                continue;
            objs.add(obj);
        }
        return objs.toArray(new GameObject[objs.size()]);
    }

    public static GameObject[] getFirst(Filter<GameObject> filter) {
        return getFirstWithin(-1, filter);
    }

    public static GameObject getNearestWithin(int dist, Filter<GameObject> filter) {
        GameObject[] loaded = getFirstWithin(dist, filter);
        if (loaded.length == 0)
            return null;
        Arrays.sort(loaded, (o1, o2) -> o1.distance() - o2.distance());
        return loaded[0];
    }

    public static GameObject getNearest(Filter<GameObject> filter) {
        return getNearestWithin(-1, filter);
    }

    public static GameObject getNearest(String name) {
        return getNearest(o -> {
            String oName = o.getName();
            return oName != null && oName.equals(name);
        });
    }

    public static GameObject getNearest(Landmark landmark) {
        return getNearest(o -> o.getDefinition() != null && o.getLandmark() == landmark);
    }

    public static GameObject[] getNearestFrom(Locatable src, int dist, Filter<GameObject> filter) {
        List<GameObject> npcs = new ArrayList<>();
        for (GameObject npc : getLoaded()) {
            if (npc == null || dist != -1 && npc.distance(src) > dist || !filter.accept(npc))
                continue;
            npcs.add(npc);
        }
        return npcs.toArray(new GameObject[npcs.size()]);
    }

    public static GameObject getNearestFromWithin(Locatable src, int dist, Filter<GameObject> filter) {
        GameObject[] loaded = getNearestFrom(src, dist, filter);
        if (loaded.length == 0)
            return null;
        Arrays.sort(loaded, (o1, o2) -> o1.distance() - o2.distance());
        return loaded[0];
    }

    public static GameObject getNearestFrom(Locatable src, Filter<GameObject> filter) {
        return getNearestFromWithin(src, -1, filter);
    }
}
