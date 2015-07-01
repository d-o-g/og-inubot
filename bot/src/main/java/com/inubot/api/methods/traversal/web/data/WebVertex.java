package com.inubot.api.methods.traversal.web.data;

import com.inubot.api.oldschool.Tile;

/**
 * @author Septron
 * @since July 01, 2015
 */
public class WebVertex {

    private final int x, y, plane;

    public WebVertex(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }

    public WebVertex(int x, int y) {
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

    public Tile asTile() {
        return new Tile(x, y, plane);
    }

    @Override
    public int hashCode() {
        return plane << 0x1c | y << 0xe | x;
    }
}
