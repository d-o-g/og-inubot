/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods.traversal.graph;

import com.inubot.apis.methods.traversal.graph.data.WebVertex;

import java.util.*;
import java.util.stream.Collectors;

public class DijkstraPathfinder implements Pathfinder<WebVertex> {

    private final Set<WebVertex> unsettled, settled;
    private final Map<WebVertex, WebVertex> preds;
    private final Map<WebVertex, Integer> distances;

    public DijkstraPathfinder() {
        this.unsettled = settled = new HashSet<>();
        this.preds = new HashMap<>();
        this.distances = new HashMap<>();
    }

    @Override
    public WebVertex[] generate(WebVertex src, WebVertex dest) {
        if (src.getIndex() == dest.getIndex())
            return new WebVertex[0];
        long time = System.currentTimeMillis();
        settled.clear();
        unsettled.clear();
        distances.clear();
        preds.clear();
        distances.put(src, 0);
        unsettled.add(src);
        while (unsettled.size() > 0) {
            WebVertex top = getNearest(unsettled);
            settled.add(top);
            unsettled.remove(top);
            List<WebVertex> edges = unsettledEdgesFor(top);
            for (WebVertex target : edges) {
                if (getClosestDistance(target) > getClosestDistance(top) + getDistance(top, target)) {
                    distances.put(target, getClosestDistance(top) + getDistance(top, target));
                    preds.put(target, top);
                    unsettled.add(target);
                }
            }
        }
        List<WebVertex> path = new ArrayList<>();
        WebVertex step = dest;
        if (preds.get(step) == null)
            return null;
        path.add(step);
        while (preds.get(step) != null) {
            step = preds.get(step);
            path.add(step);
        }
        Collections.reverse(path);
        System.out.println((System.currentTimeMillis() - time) + "ms to generate path");
        return path.toArray(new WebVertex[path.size()]);
    }

    private int getDistance(WebVertex node, WebVertex target) {
        for (WebVertex edge : node.getEdges()) {
            if (edge.equals(target)) {
                return edge.getTile().distance(node.getTile());
            }
        }
        throw new RuntimeException("WTF");
    }

    private List<WebVertex> unsettledEdgesFor(WebVertex node) {
        return node.getEdges().stream().filter(edge -> !settled.contains(edge)).collect(Collectors.toList());
    }

    private WebVertex getNearest(Set<WebVertex> vertexes) {
        WebVertex closest = null;
        for (WebVertex vertex : vertexes) {
            if (closest == null || getClosestDistance(vertex) < getClosestDistance(closest)) {
                closest = vertex;
            }
        }
        return closest;
    }

    private int getClosestDistance(WebVertex dest) {
        Integer d = distances.get(dest);
        return d == null ? Integer.MAX_VALUE : d;
    }
}
