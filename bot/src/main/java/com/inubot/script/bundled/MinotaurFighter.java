/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled;

import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.util.Time;
import com.inubot.script.bundled.proscripts.framework.ProScript;

import java.util.Map;

/**
 * @author Dogerina
 * @since 18-07-2015
 */
public class MinotaurFighter extends ProScript {
    @Override
    public String getTitle() {
        return "MinotaurFighter";
    }

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    @Override
    public int loop() {
        if (Players.getLocal().getTarget() == null) {
            Npc npc = Npcs.getNearest(n -> n.getName() != null && (n.getName().equals("Wolf") || n.getName().equals("Minotaur"))
                    && !n.isDying());
            if (npc != null) {
                npc.processAction("Attack");
                Time.await(() -> Players.getLocal().getTarget() != null, 2000);
            }
        }
        return 900;
    }
}
