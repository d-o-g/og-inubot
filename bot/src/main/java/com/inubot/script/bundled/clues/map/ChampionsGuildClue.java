/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.clues.map;

import com.inubot.api.oldschool.Tile;
import com.inubot.script.bundled.clues.ClueSolver;

/**
 * @author Dogerina
 * @since 30-06-2015
 */
public class ChampionsGuildClue extends MapClue {

    public ChampionsGuildClue(ClueSolver clueSolver) {
        super(clueSolver);
    }

    @Override
    public boolean isInBuilding() {
        return false;
    }

    @Override
    public Tile getDigLocation() {
        return new Tile(3166, 3360, 0);
    }

    @Override
    public int getId() {
        return 2713;
    }
}
