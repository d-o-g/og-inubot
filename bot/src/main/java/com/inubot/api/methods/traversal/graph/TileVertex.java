/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods.traversal.graph;

public class TileVertex {

    private final int x, y, plane;

    public TileVertex(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }

    public TileVertex(int x, int y) {
        this(x, y, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlane() {
        return plane;
    }

    @Override
    public int hashCode() {
        return plane << 0x1c | y << 0xe | x;
    }
}
