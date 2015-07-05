package com.inubot.api.methods.traversal.graph.data;

import com.inubot.api.oldschool.Tile;

import java.util.ArrayList;
import java.util.List;

public class WebVertex {

    private final int index, x, y, plane;
    private final List<WebVertex> edges;
    private final int[] edgeIndexes;

    public WebVertex(int index, int x, int y, int plane, int[] edgeIndexes) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.edgeIndexes = edgeIndexes;
        this.edges = new ArrayList<>();
    }

    public WebVertex(int index, int x, int y, int plane) {
        this(index, x, y, 0, new int[0]);
    }

    public WebVertex(int index, int x, int y, int[] edgeIndexes) {
        this(index, x, y, 0, edgeIndexes);
    }

    public WebVertex(int index, int x, int y) {
        this(index, x, y, new int[0]);
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

    public Tile getTile() {
        return new Tile(x, y, plane);
    }

    @Override
    public int hashCode() {
        return plane << 0x1c | y << 0xe | x;
    }

    public int getIndex() {
        return index;
    }

    public List<WebVertex> getEdges() {
        return edges;
    }

    public int[] getEdgeIndexes() {
        return edgeIndexes;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WebVertex && ((WebVertex) o).getIndex() == index;
    }
}
