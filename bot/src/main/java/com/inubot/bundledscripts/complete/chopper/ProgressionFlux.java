/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public abstract class ProgressionFlux {

    protected final Tree next;

    public ProgressionFlux(Tree next) {
        this.next = next;
    }

    public abstract boolean canProgress();
}
