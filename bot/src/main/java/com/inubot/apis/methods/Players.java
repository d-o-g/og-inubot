/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods;

import com.inubot.Inubot;
import com.inubot.apis.oldschool.Player;
import com.inubot.client.natives.RSPlayer;

import java.util.ArrayList;
import java.util.List;

public class Players {

    public static final int MAX_PLAYERS = 2048;
    public static final int LOCAL_PLAYER_INDEX = 2047;

    public static Player getLocal() {
        RSPlayer player = Inubot.getInstance().getClient().getPlayer();
        return player != null ? new Player(player, LOCAL_PLAYER_INDEX) : null;
    }

    public static RSPlayer[] raw() {
        return Inubot.getInstance().getClient().getPlayers();
    }

    public static Player[] getLoaded() {
        List<Player> players = new ArrayList<>();
        RSPlayer[] raws = raw();
        if (raws == null || raws.length == 0)
            return new Player[0];
        for (int i = 0; i < raws.length; i++) {
            RSPlayer player = raws[i];
            if (player == null)
                continue;
            players.add(new Player(player, i));
        }
        return players.toArray(new Player[players.size()]);
    }
}
