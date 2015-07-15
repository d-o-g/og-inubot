/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods.traversal.graph;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.methods.traversal.Path;
import com.inubot.api.methods.traversal.graph.data.ObjectVertex;
import com.inubot.api.methods.traversal.graph.data.WebVertex;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;

import java.util.*;

public class WebPath implements Path {

    private final WebVertex src, dest;
    private final WebVertex[] vertices;

    private WebPath(WebVertex src, WebVertex dest) {
        this.src = src;
        this.dest = dest;
        if (src == null || dest == null)
            throw new RuntimeException("Failed to find vertices!");
        WebVertex[] path = Movement.getWeb().getPathfinder().generate(src, dest);
        if (path == null)
            throw new RuntimeException("Failed to find path!");
        this.vertices = path;
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
        Tile[] tiles = new Tile[vertices.length];
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
                    if (vertex.getName() != null) {
                        Movement.walkTo(vertex.getTile());
                    } else {
                        throw new IllegalStateException("Bad ObjectVertex (" + vertex.getIndex() + ") on web?");
                    }
                }

                if (object != null) {
                    if (vertex.getAction() == null) {
                        object.processAction(vertex.getAction());
                    } else {
                        String[] actions = object.getActions();
                        if (actions != null && actions.length > 0) {
                            object.processAction(actions[0]);
                        }
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
        if (vertices.length == 0)
            return null;
        if (vertices.length == 1)
            return vertices[0];
        int dist = Integer.MAX_VALUE;
        int nearIndex = -1;
        for (int i = 0; i < vertices.length; i++) {
            int temp = vertices[i].getTile().distance(src.getTile());
            if (temp < dist) {
                dist = temp;
                nearIndex = i;
            }
        }
        return nearIndex == vertices.length - 1 ? dest : vertices[nearIndex + 1];
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
