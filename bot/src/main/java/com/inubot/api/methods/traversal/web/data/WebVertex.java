package com.inubot.api.methods.traversal.web.data;

import com.inubot.api.oldschool.Tile;

import java.util.*;

/**
 * @author Septron
 * @since July 01, 2015
 */
public class WebVertex implements Comparator<WebVertex> {

    private final int index, x, y, plane;
    private final List<WebVertex> edges;
    private final int[] edgeIndexes;
    private final int[] distances;
    private int shortestDistance = Integer.MAX_VALUE;
    private WebVertex predecessor;

    public WebVertex(int index, int x, int y, int plane, int[] edgeIndexes) {
        this.index = index;
        this.x = x;
        this.y = y;
        this.plane = plane;
        this.edgeIndexes = edgeIndexes;
        this.distances = new int[edgeIndexes.length];
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

    public void calculateDistances() {
        for (int i = 0; i < distances.length; i++) {
            if (edges.size() > i) {
                WebVertex edge = edges.get(i);
                distances[i] = getTile().distance(edge.getTile());
            }
        }
    }

    public int[] getDistances() {
        return distances;
    }

    public int getShortestDistance() {
        return shortestDistance;
    }

    public void setShortestDistance(int shortestDistance) {
        this.shortestDistance = shortestDistance;
    }

    public WebVertex getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(WebVertex predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public int compare(WebVertex o1, WebVertex o2) {
        return Integer.compare(o1.getShortestDistance(), o2.getShortestDistance());
    }
}
