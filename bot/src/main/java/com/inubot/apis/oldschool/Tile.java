/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool;

import com.inubot.apis.methods.Game;
import com.inubot.apis.methods.Players;
import com.inubot.apis.methods.Projection;

import java.awt.*;
import java.util.Objects;

public class Tile implements Locatable {

    private int x, y, plane;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Tile(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
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

    public int getRegionX() {
        return getX() - Game.getRegionBaseX();
    }

    public int getRegionY() {
        return getY() - Game.getRegionBaseY();
    }

    public int getStrictX() {
        return getRegionX() << 7;
    }

    public int getStrictY() {
        return getRegionY() << 7;
    }

    public Point toViewport(double dx, double dy, int height) {
        int x = getRegionX() << 7;
        int y = getRegionY() << 7;
        x += 128 * dx;
        y += 128 * dy;
        return Projection.groundToViewport(x, y, height);
    }

    public Tile derive(int x, int y) {
        return new Tile(this.x + x, this.y + y);
    }

    public void draw(Graphics2D g) {
        Polygon polygon = new Polygon();
        Point[] points = {toViewport(0, 0, 0), toViewport(1, 0, 0), toViewport(1, 1, 0), toViewport(0, 1, 0)};
        for (Point p : points) {
            if (p == null)
                return;
            polygon.addPoint(p.x, p.y);
        }
        g.draw(polygon);
    }

    public boolean isInBuilding() {
        int regionX = getRegionX();
        int regionY = getRegionY();
        if (regionX <= 0 || regionX >= 104 || regionY <= 0 || regionY >= 104)
            return false;
        return Game.getClient().getRenderRules()[Game.getPlane()][regionX][regionY] >= 4;
    }

    @Override
    public Tile getLocation() {
        return this;
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return (int) Projection.distance(this, Players.getLocal());
    }

    public int hashCode() {
        return Objects.hash(x, y, plane);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile t = (Tile) obj;
            return t.x == x && t.y == y && t.plane == plane;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d, %d]", x, y, plane);
    }
}
