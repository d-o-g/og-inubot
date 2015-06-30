/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.clues.map;

import com.inubot.api.oldschool.Tile;
import com.inubot.script.bundled.clues.Clue;
import com.inubot.script.bundled.clues.ClueSolver;

/**
 * @author Dogerina
 * @since 30-06-2015
 */
public abstract class MapClue extends Clue {

    public MapClue(ClueSolver clueSolver) {
        super(clueSolver);
    }

    public abstract boolean isInBuilding();
    public abstract Tile getDigLocation();
}
