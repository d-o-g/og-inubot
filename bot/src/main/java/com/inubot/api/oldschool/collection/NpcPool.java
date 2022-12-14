/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.collection;

import com.inubot.api.oldschool.Npc;

import java.util.ArrayList;

public class NpcPool extends PathingEntityPool<Npc, NpcPool> {

    public NpcPool() {
        super(new ArrayList<>());
    }

    public NpcPool(final Npc... elements) {
        super(elements);
    }

    public NpcPool(final Iterable<Npc>... elements) {
        super(elements);
    }
}
