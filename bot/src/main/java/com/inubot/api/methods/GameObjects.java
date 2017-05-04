/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.GameObject.Landmark;
import com.inubot.api.oldschool.collection.GameObjectPool;
import com.inubot.api.util.filter.*;
import com.inubot.client.natives.oldschool.*;

import java.util.*;

public class GameObjects {

    private static GameObjectPool pool;

    /**
     * @param cached whether to return the current cached pool or not
     * @return A {@link com.inubot.api.oldschool.collection.GameObjectPool} which can be used for selection
     * by chain calls
     */
    public static GameObjectPool getPool(boolean cached) {
        return cached && pool != null ? pool : (pool = new GameObjectPool(getLoaded()));
    }

    /**
     * @return A {@link com.inubot.api.oldschool.collection.GameObjectPool} which can be used for selection
     * by chain calls
     */
    public static GameObjectPool getPool() {
        return pool;
    }

    /**
     * @param rx The region x position
     * @param ry The region y position
     * @param z The floor level
     * @return An array of {@link com.inubot.api.oldschool.GameObject}'s at the specified position
     */
    public static GameObject[] getLoadedAt(int rx, int ry, int z) {
        if (rx > 104 || rx < 0 || ry > 104 || ry < 0 || z < 0 || z > 3) {
            return new GameObject[0];
        }
        List<GameObject> objs = new ArrayList<>();
        RSTile[][][] tiles = Inubot.getInstance().getClient().getRegion().getTiles();
        if (tiles == null) {
            return new GameObject[0];
        }
        RSTile tile = tiles[z][rx][ry];
        if (tile == null) {
            return new GameObject[0];
        }
        for (RSTileComponent component : tile.getComponents()) {
            if (component instanceof RSGameObject) {
                objs.add(new GameObject((RSGameObject) component));
            }
        }
        return objs.toArray(new GameObject[objs.size()]);
    }

    public static GameObject getTopOn(Tile tile) {
        int rx = tile.getRegionX();
        int ry = tile.getRegionY();
        int z = Game.getPlane();
        RSTile[][][] tiles = Inubot.getInstance().getClient().getRegion().getTiles();
        if (tiles == null) {
            return null;
        }
        RSTile _tile = tiles[z][rx][ry];
        if (_tile == null) {
            return null;
        }
        for (RSTileComponent tc : _tile.getComponents()) {
            if (tc instanceof RSGameObject) {
                return new GameObject((RSGameObject) tc);
            }
        }
        return null;
    }

    /**
     * @param filter The {@link com.inubot.api.util.filter.Filter} which should be used to select elements
     * @return an array of {@link com.inubot.api.oldschool.GameObject}'s in the loaded region accepted
     * by the given filter
     */
    public static GameObject[] getLoaded(Filter<GameObject> filter) {
        List<GameObject> objs = new ArrayList<>();
        int z = Game.getPlane();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                for (GameObject obj : getLoadedAt(x, y, z)) {
                    if (!filter.accept(obj)) {
                        continue;
                    }
                    objs.add(obj);
                }
            }
        }
        return objs.toArray(new GameObject[objs.size()]);
    }

    /**
     * @return An array of {@link com.inubot.api.oldschool.GameObject}'s in the loaded region
     */
    public static GameObject[] getLoaded() {
        return getLoaded(Filter.always());
    }

    private static GameObject[] getFirstWithin(int dist, Filter<GameObject> filter) {
        List<GameObject> objs = new ArrayList<>();
        for (GameObject obj : getLoaded()) {
            if (obj == null || dist != -1 && obj.distance() > dist || !filter.accept(obj))
                continue;
            objs.add(obj);
        }
        return objs.toArray(new GameObject[objs.size()]);
    }

    private static GameObject[] getFirst(Filter<GameObject> filter) {
        return getFirstWithin(-1, filter);
    }

    public static GameObject getNearestWithin(int dist, Filter<GameObject> filter) {
        GameObject[] loaded = getFirstWithin(dist, filter);
        if (loaded.length == 0)
            return null;
        Arrays.sort(loaded, (o1, o2) -> o1.distance() - o2.distance());
        return loaded[0];
    }

    /**
     * @param filter
     * @return The nearest {@link com.inubot.api.oldschool.GameObject} accepted by the given {@link com.inubot.api.util.filter.Filter}
     */
    public static GameObject getNearest(Filter<GameObject> filter) {
        return getNearestWithin(-1, filter);
    }

    /**
     * @param names
     * @return The nearest {@link com.inubot.api.oldschool.GameObject} matching the specified names
     */
    public static GameObject getNearest(String... names) {
        return getNearest(new NameFilter<GameObject>(names));
    }

    /**
     * @param ids
     * @return The nearest {@link com.inubot.api.oldschool.GameObject} matching the specified ids
     */
    public static GameObject getNearest(int... ids) {
        return getNearest(new IdFilter<GameObject>(ids));
    }

    /**
     * @param landmark
     * @return The nearest {@link com.inubot.api.oldschool.GameObject} matching the given {@link com.inubot.api.oldschool.GameObject.Landmark}
     */
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
