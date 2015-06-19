/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.farm.shell.commands;

import com.inubot.api.methods.Players;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Player;

/**
 * @author unsigned
 * @since 09-06-2015
 */
public class TradeCommand extends ShellCommand {
    @Override
    public String getName() {
        return "trade";
    }

    @Override
    public String process(String... args) {
        if (args.length == 0)
            return "No args?";
        String target = args[0];
        if (target == null)
            return "null target name specified?";
        for (Player player : Players.getLoaded()) {
            if (player.getName() != null && player.getName().toLowerCase().contains(target.toLowerCase())) {
                player.processAction("Trade with");
                return "Traded with " + player.getName();
            }
        }
        return "Failed to find player..";
    }

    @Override
    public String getUsageInfo() {
        return "trade <targetname>";
    }
}
