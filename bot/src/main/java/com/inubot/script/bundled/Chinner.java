/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.script.Script;

/**
 * @author Dogerina
 * @since 13-07-2015
 */
public class Chinner extends Script {

    private Tile start;

    @Override
    public int loop() { //TODO make it attack centre npc instead of just 'afking'
        if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            return 800;
        }
        if (Prayers.getPoints() < 5) {
            WidgetItem pot = Inventory.getFirst(item -> item.getName().startsWith("Prayer pot"));
            if (pot != null)
                pot.processAction("Drink");
        }
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 12) {
            WidgetItem lob = Inventory.getFirst("Manta ray");
            if (lob != null)
                lob.processAction("Eat");
        }
        if (start != null) {
            if (!Players.getLocal().getLocation().equals(start))
                Movement.walkTo(start);
        } else {
            start = Players.getLocal().getLocation();
        }
        return 800;
    }
}
