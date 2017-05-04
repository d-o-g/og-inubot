/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.hunter;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.awt.*;

@Manifest(name = "Falconry PRO", developer = "", desc = "Does falconry for hunter experience")
public class FalconryPRO extends Script implements Paintable {

    private int startExp;
    private StopWatch runtime;

    private static final int GLOVE_ID = 10023;

    public boolean setup() {
        startExp = Skills.getExperience(Skill.HUNTER);
        runtime = new StopWatch(0);
        return true;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 600;

        if (Players.getLocal().getAnimation() != -1)
            return 500;
        /*
        if (!contains(ItemTables.EQUIPMENT, GLOVE_ID)) {
            Npc naziguy = Npcs.getNearest("Matthias");
            if (naziguy != null) {
                naziguy.processAction("Talk-to");
            }
            return 500;
        }*/
        if (Inventory.getCount() > 20)
            Inventory.dropAllExcept(new NameFilter<>("Coins"));
        com.inubot.api.oldschool.Character<?> target;
        if ((target = HintArrow.getTarget()) == null || !target.getName().contains("Gyr")) {
            Npc kebbit = Npcs.getNearest("Spotted kebbit");
            if (kebbit != null)
                kebbit.processAction("Catch");
        } else {
            target.processAction("Retrieve");
            return 600;
        }
        return 600;
    }

    private boolean contains(int tablekey, int id) {
        for (int id0 : ItemTables.getIdsIn(tablekey)) {
            if (id0 == id)
                return true;
        }
        return false;
    }

    @Override
    public void render(Graphics2D g) {
        g.drawString("Runtime: " + runtime.toElapsedString(), 30, 30);
        g.drawString("Exp: " + (Skills.getExperience(Skill.HUNTER) - startExp), 30, 50);
    }
}
