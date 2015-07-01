package com.inubot.api.methods.traversal.web;

/**
 * @author Septron
 * @since July 01, 2015
 */
public interface Heuristic<T> {
    double getWeight(T src, T dest);
}
