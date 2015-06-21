/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Paintable;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author unsigned
 * @since 21-06-2015
 */
public class NMZAfker extends Script implements Paintable {

    private int start;

    @Override
    public boolean setup() {
        super.setForceIdleTimeClick(false);
        start = Skills.getExperience(Skill.DEFENCE);
        return true;
    }

    @Override
    public int loop() {
        if (Prayers.getPoints() < 5) {
            WidgetItem pot = Inventory.getFirst(item -> item.getName().startsWith("Prayer pot"));
            if (pot != null)
                pot.processAction("Drink");
        }
        if (Skills.getCurrentLevel(Skill.HITPOINTS) < 15) {
            WidgetItem lob = Inventory.getFirst("Lobster");
            if (lob != null)
                lob.processAction("Eat");
        }
        return 1000;
    }

    @Override
    public void render(Graphics2D g) {
        int xp = Skills.getExperience(Skill.DEFENCE) - start;
        g.drawString("Exp: " + xp, 20, 20);
    }
}
