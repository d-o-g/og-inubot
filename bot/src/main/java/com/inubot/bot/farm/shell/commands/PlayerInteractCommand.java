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
public class PlayerInteractCommand extends ShellCommand {
    @Override
    public String getName() {
        return "playerinteract";
    }

    @Override
    public String process(String... args) {
        if (args.length < 2)
            return "Wrong number of args you fucking pansy";
        String target = args[0];
        String action = args[1];
        if (target == null || action == null)
            return "null arg?";
        for (Player player : Players.getLoaded()) {
            if (player.getName() != null && player.getName().toLowerCase().contains(target.toLowerCase())) {
                player.processAction(action);
                return "Interacted " + action + " on player " + player.getName();
            }
        }
        return "Failed to find player";
    }

    @Override
    public String getUsageInfo() {
        return "playerinteract <target> <action>";
    }
}
