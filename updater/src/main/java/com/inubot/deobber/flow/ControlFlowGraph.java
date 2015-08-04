/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber.flow;

import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Dogerina
 * @since 03-08-2015
 */
public class ControlFlowGraph {

    private final List<GraphNode> nodes;

    public ControlFlowGraph(GraphNode... nodes) {
        this.nodes = Arrays.asList(nodes);
        assert nodes.length >= 3;
        assert getEntryPoint().getNodeType() == GraphNode.Type.EntryPoint;
        assert getRegularExit().getNodeType() == GraphNode.Type.RegularExit;
        assert getExceptionalExit().getNodeType() == GraphNode.Type.ExceptionalExit;
    }

    public static GraphNode findCommonDominator(GraphNode a, GraphNode b) {
        Set<GraphNode> path1 = new LinkedHashSet<>();
        GraphNode node1 = a;
        GraphNode node2 = b;
        while (node1 != null && path1.add(node1)) {
            node1 = node1.getImmediateDominator();
        }
        while (node2 != null) {
            if (path1.contains(node2)) {
                return node2;
            }
            node2 = node2.getImmediateDominator();
        }
        throw new IllegalStateException("No common dominator found!");
    }

    private static String nodeName(GraphNode node) {
        String name = "node" + node.getBlockIndex();
        if (node.getNodeType() == GraphNode.Type.EndFinally) {
            name += "_ef";
        }
        return name;
    }

    public GraphNode getEntryPoint() {
        return nodes.get(0);
    }

    public GraphNode getRegularExit() {
        return nodes.get(1);
    }

    public GraphNode getExceptionalExit() {
        return nodes.get(2);
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public void resetVisited() {
        for (GraphNode node : nodes) {
            node.setVisited(false);
        }
    }

    public void computeDominance() {
        computeDominance(new AtomicBoolean(false));
    }

    public void computeDominance(AtomicBoolean cancelled) {
        GraphNode entryPoint = getEntryPoint();
        entryPoint.setImmediateDominator(entryPoint);
        AtomicBoolean changed = new AtomicBoolean(true);
        while (changed.get()) {
            changed.set(false);
            resetVisited();
            if (cancelled.get()) {
                throw new CancellationException();
            }
            entryPoint.traversePreOrder(
                    GraphNode::getSuccessors,
                    b -> {
                        if (b == entryPoint) {
                            return;
                        }
                        GraphNode newImmediateDominator = null;
                        for (GraphNode p : b.getPredecessors()) {
                            if (p.isVisited() && p != b) {
                                newImmediateDominator = p;
                                break;
                            }
                        }
                        if (newImmediateDominator == null) {
                            throw new IllegalStateException("Could not compute new immediate dominator!");
                        }
                        for (GraphNode p : b.getPredecessors()) {
                            if (p != b && p.getImmediateDominator() != null) {
                                newImmediateDominator = findCommonDominator(p, newImmediateDominator);
                            }
                        }
                        if (b.getImmediateDominator() != newImmediateDominator) {
                            b.setImmediateDominator(newImmediateDominator);
                            changed.set(true);
                        }
                    }
            );
        }

        entryPoint.setImmediateDominator(null);

        for (GraphNode node : nodes) {
            GraphNode immediateDominator = node.getImmediateDominator();

            if (immediateDominator != null) {
                immediateDominator.getDominatorTreeChildren().add(node);
            }
        }
    }

    public final void computeDominanceFrontier() {
        resetVisited();
        getEntryPoint().traversePostOrder(
                GraphNode::getDominatorTreeChildren,
                n -> {
                    Set<GraphNode> dominanceFrontier = n.getDominanceFrontier();
                    dominanceFrontier.clear();
                    for (GraphNode s : n.getSuccessors()) {
                        if (s.getImmediateDominator() != n) {
                            dominanceFrontier.add(s);
                        }
                    }
                    for (GraphNode child : n.getDominatorTreeChildren()) {
                        for (GraphNode p : child.getDominanceFrontier()) {
                            if (p.getImmediateDominator() != n) {
                                dominanceFrontier.add(p);
                            }
                        }
                    }
                }
        );
    }
}
