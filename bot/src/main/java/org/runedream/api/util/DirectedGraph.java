/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.util;

import java.util.*;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class DirectedGraph<V, E> extends HashMap<V, Set<E>> implements Iterable<V> {

    @Override
    public final Iterator<V> iterator() {
        return super.keySet().iterator();
    }

    public final boolean containsVertex(final V vertex) {
        return super.containsKey(vertex);
    }

    public final boolean containsEdge(final V vertex, final E edge) {
        return super.containsKey(vertex) && super.get(vertex).contains(edge);
    }

    public final boolean addVertex(final V vertex) {
        if (super.containsKey(vertex))
            return false;
        super.put(vertex, new HashSet<>());
        return true;
    }

    public final void addEdge(final V vertex, final E edge) {
        if (!super.containsKey(vertex))
            return;
        super.get(vertex).add(edge);
    }

    public final void removeEdge(final V vertex, final E edge) {
        if (!super.containsKey(vertex))
            return;
        super.get(vertex).remove(edge);
    }

    public final Set<E> getEdgesOf(final V vertex) {
        return Collections.unmodifiableSet(super.get(vertex));
    }

    public final void merge(final DirectedGraph<V, E> graph) {
        super.putAll(graph);
    }
}