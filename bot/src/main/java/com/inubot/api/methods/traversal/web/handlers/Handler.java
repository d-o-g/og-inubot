package com.inubot.api.methods.traversal.web.handlers;

import com.inubot.api.oldschool.Tile;

/**
 * @author Septron
 * @since July 01, 2015
 */
public abstract class Handler {

    protected final Tile target;

    public Handler(Tile target) {
        this.target = target;
    }

    public abstract double getCost();
}
