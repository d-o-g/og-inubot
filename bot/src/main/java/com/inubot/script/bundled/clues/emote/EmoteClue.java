/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.clues.emote;

import com.inubot.script.bundled.clues.Clue;
import com.inubot.script.bundled.clues.ClueSolver;

/**
 * @author Dogerina
 * @since 30-06-2015
 */
public abstract class EmoteClue extends Clue {

    public static final int EMOTE_PARENT_INDEX = 216;
    public static final int EMOTE_CHILD_INDEX = 1;

    public EmoteClue(ClueSolver clueSolver) {
        super(clueSolver);
    }

    public abstract String getInstructions();
    public abstract String[] getEquipment();
    public abstract int getEmoteIndex(); //grandchild widget index
}
