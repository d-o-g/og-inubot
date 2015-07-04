/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool.collection;

import com.inubot.apis.oldschool.GameObject;

import java.util.ArrayList;
import java.util.List;

public class GameObjectPool extends IdentifiableEntityPool<GameObject, GameObjectPool> {

    public GameObjectPool(final GameObject... elements) {
        super(elements);
    }

    public GameObjectPool() {
        super(new ArrayList<>());
    }

    public GameObjectPool(final List<GameObject>... elements) {
        super(elements);
    }
}

