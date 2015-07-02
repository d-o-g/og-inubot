package com.inubot.api.methods.traversal.graph;

import com.inubot.api.methods.traversal.graph.data.WebVertex;

public interface Pathfinder<T extends WebVertex> {
    T[] generate(T src, T dest);
}
