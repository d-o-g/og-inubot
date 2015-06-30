/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.clues.emote;

import com.inubot.script.bundled.clues.ClueSolver;

/**
 * @author Dogerina
 * @since 30-06-2015
 */
public class PartyRoomClue extends EmoteClue {

    public PartyRoomClue(ClueSolver clueSolver) {
        super(clueSolver);
    }

    @Override
    public String getInstructions() {
        return "Dance in the Party Room. Equip a steel full helmet, steel platebody and an iron plateskirt.";
    }

    @Override
    public String[] getEquipment() {
        return new String[]{"Steel full helm", "Steel platebody", "Iron plateskirt"};
    }

    @Override
    public int getEmoteIndex() {
        return 12;
    }

    @Override
    public int getId() {
        return 10208;
    }
}
