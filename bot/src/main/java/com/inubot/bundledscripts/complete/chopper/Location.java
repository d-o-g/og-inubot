/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.oldschool.Area;
import com.inubot.api.oldschool.Tile;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public enum Location {

    LUMBY(new Area(new Tile(-1, -1), new Tile(-1, -1)), Tree.REGULAR);

    private final Area treeArea;
    private final int safeCombatLevel;
    private final Tree[] availableTrees;

    private Location(Area treeArea, int safeCombatLevel, Tree... availableTrees) {
        this.treeArea = treeArea;
        this.safeCombatLevel = safeCombatLevel;
        this.availableTrees = availableTrees;
    }

    private Location(Area treeArea, Tree... availableTrees) {
        this(treeArea, 0, availableTrees);
    }//

    public Area getTreeArea() {
        return treeArea;
    }

    public int getMinimumCombatLevel() {
        return safeCombatLevel;
    }

    public Tree[] getAvailableTrees() {
        return availableTrees;
    }
}
