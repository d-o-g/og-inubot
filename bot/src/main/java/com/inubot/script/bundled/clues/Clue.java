/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.clues;

/**
 * @author Dogerina
 * @since 30-06-2015
 */
public abstract class Clue {

    protected final ClueSolver clueSolver;

    public Clue(ClueSolver clueSolver) {
        this.clueSolver = clueSolver;
    }

    public abstract int getId();
}
