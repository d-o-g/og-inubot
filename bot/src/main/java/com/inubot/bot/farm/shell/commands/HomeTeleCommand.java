/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.farm.shell.commands;

import com.inubot.api.methods.Client;

/**
 * @author unsigned
 * @since 03-05-2015
 * Note: This is an unsafe command, it requires for the character to be out of combat and the home teleport spell to be ready
 */
public class HomeTeleCommand extends ShellCommand {
    @Override
    public String getName() {
        return "home";
    }

    @Override
    public String process(String... args) {
        Client.processAction(1, -1, 14286849, 57, "Cast", "Lumbridge Home Teleport", 50, 50);
        return "Teleporting to lumbridge";
    }

    @Override
    public String getUsageInfo() {
        return "home - Casts home teleport.";
    }
}
