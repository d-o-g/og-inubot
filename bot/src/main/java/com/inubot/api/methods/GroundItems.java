/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.GroundItem;
import com.inubot.api.oldschool.NodeDeque;
import com.inubot.api.oldschool.collection.GroundItemPool;
import com.inubot.api.util.filter.*;
import com.inubot.client.natives.oldschool.*;

import java.util.*;

public class GroundItems {

    private static GroundItemPool pool;

    public static GroundItemPool getPool(boolean cached) {
        return cached && pool != null ? pool : (pool = new GroundItemPool(getLoaded()));
    }

    /**
     * @return A {@link com.inubot.api.oldschool.collection.GroundItemPool} for selection
     */
    public static GroundItemPool getPool() {
        return getPool(false);
    }

    /**
     * @param filter
     * @return An array of loaded {@link com.inubot.api.oldschool.GroundItem}'s accepted by the {@link com.inubot.api.util.filter.Filter}
     */
    public static GroundItem[] getLoaded(Filter<GroundItem> filter) {
        List<GroundItem> items = new ArrayList<>();
        //for (RSNodeDeque[][] deques : Inubot.getInstance().getClient().getGroundItems()) {
        RSNodeDeque[][][] bonecodeLicksChinkyDicks = Inubot.getInstance().getClient().getGroundItems();
        for (int i = 0; i < bonecodeLicksChinkyDicks.length; i++) {
            RSNodeDeque[][] deques = bonecodeLicksChinkyDicks[i];
            for (RSNodeDeque[] xd : deques) {
                for (RSNodeDeque deque : xd) {
                    if (deque == null || deque.getTail() == null || deque.getTail().getNext() == null)
                        continue;
                    NodeDeque nd = new NodeDeque(deque);
                    for (RSNode node : nd) {
                        if (node != null && node instanceof RSItem) {
                            GroundItem wrapped = new GroundItem((RSItem) node, i);
                            if (!filter.accept(wrapped))
                                continue;
                            items.add(wrapped);
                        }
                    }
                }
            }
        }
        return items.toArray(new GroundItem[items.size()]);
    }

    /**
     * @return An array of loaded {@link com.inubot.api.oldschool.GroundItem}'s
     */
    public static GroundItem[] getLoaded() {
        return getLoaded(Filter.always());
    }

    private static GroundItem[] getWithin(int dist, Filter<GroundItem> filter) {
        List<GroundItem> items = new ArrayList<>();
        for (GroundItem npc : getLoaded()) {
            if (npc == null || dist != -1 && npc.distance() > dist || !filter.accept(npc))
                continue;
            items.add(npc);
        }
        return items.toArray(new GroundItem[items.size()]);
    }

    private static GroundItem[] getFirst(Filter<GroundItem> filter) {
        return getWithin(-1, filter);
    }

    /**
     * @param dist
     * @param filter
     * @return The nearest {@link com.inubot.api.oldschool.GroundItem} accepted by the given {@link com.inubot.api.util.filter.Filter} within the specified distance
     */
    public static GroundItem getNearestWithin(int dist, Filter<GroundItem> filter) {
        GroundItem[] loaded = getWithin(dist, filter);
        if (loaded.length == 0)
            return null;
        Arrays.sort(loaded, (o1, o2) -> Integer.compare(o1.distance(), o2.distance()));
        return loaded.length > 0 ? loaded[0] : null;
    }

    /**
     * @param filter
     * @return The nearest {@link com.inubot.api.oldschool.GroundItem} accepted by the given {@link com.inubot.api.util.filter.Filter}
     */
    public static GroundItem getNearest(Filter<GroundItem> filter) {
        return getNearestWithin(-1, filter);
    }

    /**
     * @param names
     * @return The nearest {@link com.inubot.api.oldschool.GroundItem} with the given name
     */
    public static GroundItem getNearest(String... names) {
        return getNearest(new NameFilter<GroundItem>(names));
    }

    /**
     * @param ids
     * @return The nearest {@link com.inubot.api.oldschool.GroundItem} with the given id
     */
    public static GroundItem getNearest(int... ids) {
        return getNearest(new IdFilter<GroundItem>(ids));
    }
}
