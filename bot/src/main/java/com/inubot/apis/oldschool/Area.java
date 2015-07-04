/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private final Tile start;
    private final Tile end;
    private final List<Tile> tiles;
    private final int floorLevel;

    public Area(final Tile start, final Tile end, final int floorLevel) {
        this.start = start;
        this.end = end;
        this.floorLevel = floorLevel;
        this.tiles = new ArrayList<>();
        int startX = getStart().getX(), startY = getStart().getY();
        int endX = getEnd().getX(), endY = getEnd().getY();
        for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
            for (int y = Math.max(startY, endY); y >= Math.min(startY, endY); y--) {
                tiles.add(new Tile(x, y, floorLevel));
            }
        }
    }

    public Area(final Tile start, final Tile end) {
        this(start, end, 0);
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public int getPlane() {
        return floorLevel;
    }

    public Tile getStart() {
        return start;
    }

    public Tile getEnd() {
        return end;
    }

    public int getWidth() {
        return 1 + Math.max(getStart().getX(), getEnd().getX()) - Math.min(getStart().getX(), getEnd().getX());
    }

    public int getHeight() {
        return 1 + Math.max(getStart().getY(), getEnd().getY()) - Math.min(getStart().getY(), getEnd().getY());
    }

    public boolean contains(Locatable l) {
        for (Tile tile : getTiles()) {
            if (l.getLocation().equals(tile))
                return true;
        }
        return false;
    }
}

