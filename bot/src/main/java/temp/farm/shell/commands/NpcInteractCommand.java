/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package temp.farm.shell.commands;

import com.inubot.api.methods.Npcs;
import com.inubot.api.oldschool.Npc;

/**
 * @author unsigned
 * @since 09-06-2015
 */
public class NpcInteractCommand extends ShellCommand {
    @Override
    public String getName() {
        return "npcinteract";
    }

    @Override
    public String process(String... args) {
        if (args.length < 2)
            return "Wrong number of args you fucking pansy";
        String target = args[0];
        String action = args[1];
        if (target == null || action == null)
            return "null arg?";
        for (Npc npc : Npcs.getLoaded()) {
            if (npc.getName() != null && npc.getName().toLowerCase().contains(target.toLowerCase())) {
                npc.processAction(action);
                return "Interacted " + action + " on npc " + npc.getName();
            }
        }
        return "Failed to find npc";
    }

    @Override
    public String getUsageInfo() {
        return "npcinteract <target> <action>";
    }
}
