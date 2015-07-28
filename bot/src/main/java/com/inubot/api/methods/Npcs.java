/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.Locatable;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.collection.NpcPool;
import com.inubot.api.util.filter.*;
import com.inubot.client.natives.oldschool.RSNpc;

import java.util.*;

public class Npcs {

    private static NpcPool pool;

    public static NpcPool getPool(boolean cached) {
        return cached && pool != null ? pool : (pool = new NpcPool(getLoaded()));
    }

    public static NpcPool getPool() {
        return pool;
    }

    public static RSNpc[] internal() {
        return Inubot.getInstance().getClient().getNpcs();
    }

    public static Npc[] getLoaded(Filter<Npc> filter) {
        RSNpc[] raws = internal();
        if (raws == null || raws.length == 0)
            return new Npc[0];
        int[] indices = Inubot.getInstance().getClient().getNpcIndices();
        if (indices == null || indices.length == 0)
            return new Npc[0];
        List<Npc> npcs = new ArrayList<>(indices.length);
        for (int index : indices) {
            RSNpc raw = raws[index];
            if (raw == null)
                continue;
            Npc npc = new Npc(raw, index);
            if (!filter.accept(npc))
                continue;
            npcs.add(npc);
        }
        return npcs.toArray(new Npc[npcs.size()]);
    }

    public static Npc[] getLoaded() {
        return getLoaded(Filter.always());
    }

    private static Npc[] getFirstWithin(int dist, Filter<Npc> filter) {
        List<Npc> npcs = new ArrayList<>();
        for (Npc npc : getLoaded()) {
            if (npc == null || dist != -1 && npc.distance() > dist || !filter.accept(npc))
                continue;
            npcs.add(npc);
        }
        return npcs.toArray(new Npc[npcs.size()]);
    }

    private static Npc[] getFirst(Filter<Npc> filter) {
        return getFirstWithin(-1, filter);
    }

    public static Npc getNearestWithin(int dist, Filter<Npc> filter) {
        Npc[] loaded = getFirstWithin(dist, filter);
        if (loaded.length == 0)
            return null;
        Arrays.sort(loaded, (o1, o2) -> o1.distance() - o2.distance());
        return loaded[0];
    }

    public static Npc[] getNearestFrom(Locatable src, int dist, Filter<Npc> filter) {
        List<Npc> npcs = new ArrayList<>();
        for (Npc npc : getLoaded()) {
            if (npc == null || dist != -1 && npc.distance(src) > dist || !filter.accept(npc))
                continue;
            npcs.add(npc);
        }
        return npcs.toArray(new Npc[npcs.size()]);
    }

    public static Npc getNearestFromWithin(Locatable src, int dist, Filter<Npc> filter) {
        Npc[] loaded = getNearestFrom(src, dist, filter);
        if (loaded.length == 0)
            return null;
        Arrays.sort(loaded, (o1, o2) -> o1.distance() - o2.distance());
        return loaded[0];
    }

    public static Npc getNearestFrom(Locatable src, Filter<Npc> filter) {
        return getNearestFromWithin(src, -1, filter);
    }

    public static Npc getNearest(Filter<Npc> filter) {
        return getNearestWithin(-1, filter);
    }

    /**
     * @param names
     * @return The nearest {@link com.inubot.api.oldschool.Npc} matching the given names
     */
    public static Npc getNearest(String... names) {
        return getNearest(new NameFilter<Npc>(names));
    }

    /**
     * @param ids
     * @return The nearest {@link com.inubot.api.oldschool.Npc} matching the given ids
     */
    public static Npc getNearest(int... ids) {
        return getNearest(new IdFilter<Npc>(ids));
    }
}
