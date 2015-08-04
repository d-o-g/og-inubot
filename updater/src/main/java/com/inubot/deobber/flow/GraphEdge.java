/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber.flow;

/**
 * @author Dogerina
 * @since 03-08-2015
 */
public class GraphEdge {

    private final GraphNode source;
    private final GraphNode target;
    private final JumpType type;

    public GraphEdge(GraphNode source, GraphNode target, JumpType type) {
        if (source == null || target == null || type == null) {
            throw new IllegalArgumentException("null args");
        }
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public GraphNode getSource() {
        return source;
    }

    public GraphNode getTarget() {
        return target;
    }

    public JumpType getType() {
        return type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof GraphEdge) {
            GraphEdge other = (GraphEdge) obj;
            return other.source == source && other.target == target;
        }
        return false;
    }

    @Override
    public String toString() {
        switch (type) {
            case Normal:
                return "#" + target.getBlockIndex();

            case JumpToExceptionHandler:
                return "e:#" + target.getBlockIndex();

            default:
                return type + ":#" + target.getBlockIndex();
        }
    }


    public static enum JumpType {

        Normal,

        JumpToExceptionHandler,

        LeaveTry,

        EndFinally
    }
}
