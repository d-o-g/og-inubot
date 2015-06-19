package org.runedream.bot.farm.shell.commands;

import org.runedream.api.methods.Game;
import org.runedream.api.methods.traversal.Movement;
import org.runedream.api.oldschool.Tile;

/**
 * Created by luckruns0ut on 03/05/15.
 */
public class WalkCommand extends ShellCommand {
    @Override
    public String getName() {
        return "walk";
    }

    @Override
    public String process(String... args) {
        if(args.length >= 2) {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            Movement.walkTo(new Tile(x, y, Game.getPlane()));
            return "Walking to " + x + ", " + y;
        }
        return null;
    }

    @Override
    public String getUsageInfo() {
        return "walk [x] [y] - Walks the player to a destination.";
    }
}
