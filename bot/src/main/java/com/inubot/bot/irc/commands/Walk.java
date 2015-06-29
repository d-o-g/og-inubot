package com.inubot.bot.irc.commands;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Tile;
import com.inubot.bot.irc.IRCCommand;

/**
 * Created by bytehound on 6/27/2015.
 */
public class Walk implements IRCCommand {
    @Override
    public String name() {
        return "walk";
    }

    @Override
    public void handle(String[] params) {
        if (params.length >= 2) {
            if (Game.isLoggedIn()) {
                int x = Integer.parseInt(params[1]);
                int y = Integer.parseInt(params[2]);
                final Tile tile = new Tile(x, y);
                while (tile.distance() > 3) {
                    Movement.walkTo(tile);
                }
            }
        }
    }
}
