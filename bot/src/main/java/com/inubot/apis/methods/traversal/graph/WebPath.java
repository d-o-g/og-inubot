/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods.traversal.graph;

import com.inubot.apis.methods.GameObjects;
import com.inubot.apis.methods.Players;
import com.inubot.apis.methods.traversal.Movement;
import com.inubot.apis.methods.traversal.Path;
import com.inubot.apis.methods.traversal.graph.data.ObjectVertex;
import com.inubot.apis.methods.traversal.graph.data.WebVertex;
import com.inubot.apis.oldschool.*;
import com.inubot.apis.util.Time;

import java.util.*;

public class WebPath implements Path {

    private final WebVertex src, dest;
    private final List<WebVertex> vertices;

    private WebPath(WebVertex src, WebVertex dest) {
        this.src = src;
        this.dest = dest;
        if (src == null || dest == null)
            throw new RuntimeException("Failed to find vertices!");
        this.vertices = new ArrayList<>();
        WebVertex[] path = Movement.getWeb().getPathfinder().generate(src, dest);
        if (path == null)
            throw new RuntimeException("Failed to find path!");
        Collections.addAll(vertices, path);
    }

    public static WebPath build(int srcIdx, int destIdx) {
        return new WebPath(Movement.getWeb().getVertex(srcIdx), Movement.getWeb().getVertex(destIdx));
    }

    public static WebPath build(int destIdx) {
        WebVertex v = Movement.getWeb().getNearestVertex();
        return v == null ? null : build(v.getIndex(), destIdx);
    }

    public static WebPath build(Locatable src, Locatable dest) {
        return new WebPath(Movement.getWeb().getNearestVertex(src), Movement.getWeb().getNearestVertex(dest));
    }

    public static WebPath build(Locatable dest) {
        return build(Players.getLocal(), dest);
    }

    @Override
    public Tile[] toArray() {
        Tile[] tiles = new Tile[vertices.size()];
        int i = 0;
        for (WebVertex vertex : vertices) {
            tiles[i] = vertex.getTile();
            i++;
        }
        return tiles;
    }

    @Override
    public boolean step(Option... options) {
        if (dest.getTile().distance(src.getTile()) <= 3)
            return true;
        for (Option option : options)
            option.handle();
        WebVertex next = getNext();
        if (next != null) {
            if (next instanceof ObjectVertex) {
                ObjectVertex vertex = (ObjectVertex) next;
                GameObject object;
                if (vertex.getName() != null) {
                    if (vertex.getAction() != null) {
                        object = GameObjects.getNearest(t -> t.getLocation().distance(vertex.getTile()) <= 2 && vertex.getName().equals(t.getName()) && t.containsAction(vertex.getAction()));
                    } else {
                        object = GameObjects.getNearest(t -> t.getLocation().distance(vertex.getTile()) <= 2 && vertex.getName().equals(t.getName()));
                    }
                } else {
                    object = GameObjects.getNearest(t -> t.getLocation().distance(vertex.getTile()) <= 2 && t.getName() != null);
                }

                if (object == null) {
                    throw new IllegalStateException("Bad ObjectVertex (" + vertex.getIndex() + ") on web?");
                }

                if (vertex.getAction() == null) {
                    object.processAction(vertex.getAction());
                } else {
                    String[] actions = object.getActions();
                    if (actions != null && actions.length > 0) {
                        object.processAction(actions[0]);
                    } else {
                        throw new IllegalStateException("Failed to find action for ObjectVertex!");
                    }
                }

            } else {
                Movement.walkTo(next.getTile());
            }
            return Time.await(() -> !Players.getLocal().isMoving() && Players.getLocal().getAnimation() == -1, 5000);
        }
        return false;
    }

    public WebVertex getNext() {
        int next = -1;
        if (vertices.size() == 0)
            return null;
        int dist = Integer.MAX_VALUE;
        int nearIndex = -1;
        for (int i = 0; i < vertices.size(); i++) {
            int temp = vertices.get(i).getTile().distance(src.getTile());
            if (temp < dist) {
                dist = temp;
                nearIndex = i;
            }
        }
        if (nearIndex == vertices.size() - 1)
            return dest;
        return vertices.size() == 1 ? vertices.get(0) : vertices.get(nearIndex + 1);
    }

    @Override
    public Iterator<Tile> iterator() {
        return new Iterator<Tile>() {
            @Override
            public boolean hasNext() {
                return getNext() != null;
            }

            @Override
            public Tile next() {
                return getNext().getTile();
            }
        };
    }
}
