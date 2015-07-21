/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled;

import com.inubot.Bot;
import com.inubot.Inubot;
import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Npc;
import com.inubot.script.Script;

import java.awt.event.KeyEvent;

/**
 * @author Dogerina
 * @since 14-07-2015
 */
public class ZooFighter extends Script {

    private static final String[] names = new String[]{"Cyclops", "Jogre", "Wolf"};

    @Override
    public int loop() {
        if (Interfaces.canContinue()) {
            Bot.getInstance().getCanvas().pressKey(KeyEvent.VK_SPACE, 200);
            Bot.getInstance().getCanvas().releaseKey(KeyEvent.VK_SPACE);
            return 1700;
        }
        if (Players.getLocal().getAnimation() != -1)
            return 1500;
        Npc npc = Npcs.getNearest(n -> {
            for (String name : names) {
                if (n != null && n.getName() != null && n.getName().equals(name))
                    return true;
            }
            return false;
        });
        if (npc != null && Players.getLocal().getTarget() == null) {
            npc.processAction("Attack");
            return 3000;
        }
        return 1500;
    }
}
