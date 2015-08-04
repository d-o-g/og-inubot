/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber.flow;

import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.*;
import java.util.function.*;

/**
 * @author Dogerina
 * @since 03-08-2015
 */
public class GraphNode implements Comparable<GraphNode> {

    public static final Predicate<GraphNode> REACHABLE_PREDICATE = GraphNode::isReachable;
    private final int blockIndex;
    private final int offset;
    private final Type type;
    private final GraphNode endFinallyNode;
    private final List<GraphNode> dominatorTreeChildren = new LinkedList<>();
    private final Set<GraphNode> dominanceFrontier = new LinkedHashSet<>();
    private final List<GraphEdge> incoming = new LinkedList<>();
    private final List<GraphEdge> outgoing = new LinkedList<>();

    private boolean visited;
    private GraphNode copyFrom;
    private GraphNode immediateDominator;
    private AbstractInsnNode start;
    private AbstractInsnNode end;
    private TryCatchBlockNode exceptionHandler;
    private Object userData;

    public GraphNode(int blockIndex, int offset, Type nodeType) {
        this.blockIndex = blockIndex;
        this.offset = offset;
        if (nodeType == null) {
            throw new IllegalArgumentException("null type");
        }
        type = nodeType;
        endFinallyNode = null;
        start = null;
        end = null;
    }

    public GraphNode(int blockIndex, AbstractInsnNode start, AbstractInsnNode end) {
        this.blockIndex = blockIndex;
        if (start == null || end == null) {
            throw new IllegalArgumentException("null instructions");
        }
        this.start = start;
        this.end = end;
        offset = start.index();
        type = Type.Normal;
        endFinallyNode = null;
    }

    public GraphNode(int blockIndex, TryCatchBlockNode exceptionHandler, GraphNode endFinallyNode) {
        this.blockIndex = blockIndex;
        if (exceptionHandler == null) {
            throw new IllegalArgumentException("null exception handler");
        }
        this.exceptionHandler = exceptionHandler;
        type = Type.CatchHandler; //TODO finally handlers
        this.endFinallyNode = endFinallyNode;
        AbstractInsnNode handlerBlock = exceptionHandler.handler;
        start = null;
        end = null;
        offset = handlerBlock.index();
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public int getOffset() {
        return offset;
    }

    public Type getNodeType() {
        return type;
    }

    public GraphNode getEndFinallyNode() {
        return endFinallyNode;
    }

    public List<GraphNode> getDominatorTreeChildren() {
        return dominatorTreeChildren;
    }

    public Set<GraphNode> getDominanceFrontier() {
        return dominanceFrontier;
    }

    public List<GraphEdge> getIncoming() {
        return incoming;
    }

    public List<GraphEdge> getOutgoing() {
        return outgoing;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isReachable() {
        return immediateDominator != null || type == Type.EntryPoint;
    }

    public GraphNode getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(GraphNode copyFrom) {
        this.copyFrom = copyFrom;
    }

    public GraphNode getImmediateDominator() {
        return immediateDominator;
    }

    public void setImmediateDominator(GraphNode immediateDominator) {
        this.immediateDominator = immediateDominator;
    }

    public AbstractInsnNode getStart() {
        return start;
    }

    public void setStart(AbstractInsnNode start) {
        this.start = start;
    }

    public AbstractInsnNode getEnd() {
        return end;
    }

    public void setEnd(AbstractInsnNode end) {
        this.end = end;
    }

    public TryCatchBlockNode getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(TryCatchBlockNode exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public boolean succeeds(GraphNode other) {
        if (other == null) {
            return false;
        }
        for (GraphEdge a_incoming : incoming) {
            if (a_incoming.getSource() == other) {
                return true;
            }
        }
        return false;
    }

    public final boolean precedes(GraphNode other) {
        if (other == null) {
            return false;
        }
        for (GraphEdge a_outgoing : outgoing) {
            if (a_outgoing.getTarget() == other) {
                return true;
            }
        }
        return false;
    }

    public Iterable<GraphNode> getPredecessors() {
        return PredecessorIterator::new;
    }

    public Iterable<GraphNode> getSuccessors() {
        return SuccessorIterator::new;
    }

    public Iterable<AbstractInsnNode> getInstructions() {
        return InstructionIterator::new;
    }

    public void traversePreOrder(Function<GraphNode, Iterable<GraphNode>> children, Consumer<GraphNode> visitAction) {
        if (visited) {
            return;
        }
        visited = true;
        visitAction.accept(this);
        for (GraphNode child : children.apply(this)) {
            child.traversePreOrder(children, visitAction);
        }
    }

    public void traversePostOrder(Function<GraphNode, Iterable<GraphNode>> children, Consumer<GraphNode> visitAction) {
        if (visited) {
            return;
        }
        visited = true;
        for (GraphNode child : children.apply(this)) {
            child.traversePostOrder(children, visitAction);
        }
        visitAction.accept(this);
    }

    public boolean dominates(GraphNode node) {
        GraphNode current = node;
        while (current != null) {
            if (current == this) {
                return true;
            }
            current = current.immediateDominator;
        }
        return false;
    }

    @Override
    public String toString() {
        switch (type) {
            case Normal: {
                System.out.printf("Block #%d%n", blockIndex);
                if (start != null) {
                    System.out.printf(": %d to %d%n", start.index(), end.index());
                }
                break;
            }
            case CatchHandler:
            case FinallyHandler: {
                System.out.printf("Block #%d: %s: ", blockIndex, type);
                //DecompilerHelpers.writeExceptionHandler(output, _exceptionHandler);
                break;
            }
            default: {
                System.out.printf("Block #%d: %s%n", blockIndex, type);
                break;
            }
        }

        if (!dominanceFrontier.isEmpty()) {
            System.out.println();
            System.out.println("DominanceFrontier: ");
            int[] blockIndexes = new int[dominanceFrontier.size()];
            int i = 0;
            for (GraphNode node : dominanceFrontier) {
                blockIndexes[i++] = node.blockIndex;
            }
            Arrays.sort(blockIndexes);
        }
        for (AbstractInsnNode instruction : getInstructions()) {
            System.out.println(Assembly.toString(instruction));
        }
        Object userData = this.userData;
        if (userData != null) {
            System.out.println(String.valueOf(userData));
        }
        return "";
    }

    @Override
    public int compareTo(GraphNode o) {
        return Integer.compare(blockIndex, o.blockIndex);
    }

    public static enum Type {
        Normal,
        EntryPoint,
        RegularExit,
        ExceptionalExit,
        CatchHandler,
        FinallyHandler,
        EndFinally
    }

    private final class PredecessorIterator implements Iterator<GraphNode> {

        private Iterator<GraphEdge> _innerIterator;

        @Override
        public boolean hasNext() {
            if (_innerIterator == null) {
                _innerIterator = incoming.listIterator();
            }

            return _innerIterator.hasNext();
        }

        @Override
        public GraphNode next() {
            if (_innerIterator == null) {
                _innerIterator = incoming.listIterator();
            }
            return _innerIterator.next().getSource();
        }
    }

    private final class SuccessorIterator implements Iterator<GraphNode> {

        private Iterator<GraphEdge> _innerIterator;

        @Override
        public boolean hasNext() {
            if (_innerIterator == null) {
                _innerIterator = outgoing.listIterator();
            }
            return _innerIterator.hasNext();
        }

        @Override
        public GraphNode next() {
            if (_innerIterator == null) {
                _innerIterator = outgoing.listIterator();
            }
            return _innerIterator.next().getTarget();
        }
    }

    private final class InstructionIterator implements Iterator<AbstractInsnNode> {

        private AbstractInsnNode _next = start;

        @Override
        public boolean hasNext() {
            return _next != null && _next.index() <= end.index();
        }

        @Override
        public AbstractInsnNode next() {
            AbstractInsnNode next = _next;
            if (next == null || next.index() > end.index()) {
                throw new NoSuchElementException();
            }
            _next = next.next();
            return next;
        }
    }
}
