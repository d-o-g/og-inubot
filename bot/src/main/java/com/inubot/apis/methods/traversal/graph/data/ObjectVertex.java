/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods.traversal.graph.data;

import com.inubot.apis.oldschool.GameObject;
import com.inubot.apis.oldschool.Tile;

public class ObjectVertex extends WebVertex {

    private final String name, action;

    public ObjectVertex(int index, int x, int y, int plane, int[] edgeIndexes, String name, String action) {
        super(index, x, y, plane, edgeIndexes);
        this.name = name;
        this.action = action;
    }

    public ObjectVertex(int index, int x, int y, int plane, String name, String action) {
        this(index, x, y, plane, new int[0], name, action);
    }

    public ObjectVertex(int index, Tile tile, int[] edgeIndexes, String name, String action) {
        this(index, tile.getX(), tile.getY(), tile.getPlane(), edgeIndexes, name, action);
    }

    public ObjectVertex(int index, Tile tile, String name, String action) {
        this(index, tile, new int[0], name, action);
    }

    public ObjectVertex(int index, GameObject object, int[] edgeIndexes, String name, String action) {
        this(index, object.getLocation(), edgeIndexes, name, action);
    }

    public ObjectVertex(int index, GameObject object, String name, String action) {
        this(index, object, new int[0], name, action);
    }

    public String getName() {
        return name;
    }

    public String getAction() {
        return action;
    }
}
