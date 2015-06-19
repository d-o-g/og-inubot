/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Player;
import com.inubot.client.natives.RSNpc;
import com.inubot.client.natives.RSPlayer;

/**
 * @author unsigned
 * @since 16-05-2015
 */
public class HintArrow {

    /**
     * Note: This will only return correctly if the hint arrow is pointed to a tile.
     * If it is pointed to a specific character, then use #getTarget
     *
     * @return The X position of the current hint arrow
     */
    public static int getX() {
        return Inubot.getInstance().getClient().getHintArrowX();
    }

    /**
     * Note: This will only return correctly if the hint arrow is pointed to a tile.
     * If it is pointed to a specific character, then use #getTarget
     *
     * @return The Y position of the current hint arrow
     */
    public static int getY() {
        return Inubot.getInstance().getClient().getHintArrowY();
    }

    /**
     * If the current hint arrow is pointed to a tile and not a character, then the methods you
     * would like to use are #getX and #getY.
     * @return the target character of the current HintArrow
     */
    public static com.inubot.api.oldschool.Character<?> getTarget() {
        int index = Inubot.getInstance().getClient().getHintArrowNpcIndex();
        if (index < 0)
            return null;
        RSNpc npc = Npcs.raw()[index];
        if (npc != null)
            return new Npc(npc, index);
        RSPlayer player = Players.raw()[index = Inubot.getInstance().getClient().getHintArrowPlayerIndex()];
        if (player != null)
            return new Player(player, index);
        return null;
    }

    public static int getType() {
        return Inubot.getInstance().getClient().getHintArrowType();
    }
}
