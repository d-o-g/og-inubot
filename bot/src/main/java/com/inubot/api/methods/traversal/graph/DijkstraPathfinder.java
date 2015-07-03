/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods.traversal.graph;

import com.inubot.api.methods.traversal.graph.data.WebVertex;

import java.util.*;

public class DijkstraPathfinder implements Pathfinder<WebVertex> {

    @Override
    public WebVertex[] generate(WebVertex src, WebVertex dest) {
        return new WebVertex[0];
    }
}
