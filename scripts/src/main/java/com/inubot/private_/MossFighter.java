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
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

@Manifest(name = "MossFighter", developer = "", desc = "")
public class MossFighter extends ProScript {

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    @Override
    public int loop() {
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 20) {
            Inventory.getFirst("Shark").processAction("Eat");
            return 3000;
        }
        if (Players.getLocal().getTarget() == null) {
            Npc npc = Npcs.getNearest(n -> n.getName() != null && (n.getName().equals("Moss giant"))
                    && Movement.isReachable(n) && !n.isDying());
            if (npc != null) {
                npc.processAction("Attack");
                Time.await(() -> Players.getLocal().getTarget() != null, 2000);
            }
        }
        return 900;
    }
}
