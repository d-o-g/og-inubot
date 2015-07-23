/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.private_;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.util.Time;
import com.inubot.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

/**
 * @author Dogerina
 * @since 18-07-2015
 */
@Manifest(name = "Minotaur", developer = "", desc = "")
public class MinowolfFighter extends ProScript {

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    @Override
    public int loop() {
        if (Players.getLocal().getTarget() == null && Skills.getCurrentLevel(Skill.HITPOINTS) > 10) {
            Npc npc = Npcs.getNearest(n -> n.getName() != null && (n.getName().equals("Minotaur"))
                    && Movement.isReachable(n) && !n.isDying());
            if (npc != null) {
                npc.processAction("Attack");
                Time.await(() -> Players.getLocal().getTarget() != null, 2000);
            }
        }
        return 900;
    }
}
