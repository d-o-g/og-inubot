/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.oldschool.Area;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public enum Location {

    ;

    private final Area treeArea;
    private final int safeCombatLevel;

    private Location(Area treeArea, int safeCombatLevel, Tree... availableTrees) {
        this.treeArea = treeArea;
        this.safeCombatLevel = safeCombatLevel;
    }

    public Area getTreeArea() {
        return treeArea;
    }

    public int getMinimumCombatLevel() {
        return safeCombatLevel;
    }
}
