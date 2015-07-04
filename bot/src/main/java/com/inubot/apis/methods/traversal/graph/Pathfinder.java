package com.inubot.apis.methods.traversal.graph;

import com.inubot.apis.methods.traversal.graph.data.WebVertex;

public interface Pathfinder<T extends WebVertex> {
    T[] generate(T src, T dest);
}
