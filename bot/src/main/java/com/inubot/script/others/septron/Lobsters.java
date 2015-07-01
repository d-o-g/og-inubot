package com.inubot.script.others.septron;

import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Random;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Septron
 * @since June 30, 2015
 */
public class Lobsters extends Script implements Paintable {

    @Override
    public void render(Graphics2D g) {

    }

    @Override
    public int loop() {
        if (Inventory.isFull()) {
            if (Inventory.getCount("Gold") > 30) {
                Npc npc = Npcs.getNearest("");
                if (npc != null)
                    npc.processAction("");
            } else {
                Npc npc = Npcs.getNearest("General Shop");
                if (npc != null)
                    npc.processAction("Trade");
            }
        } else {

        }
        return Random.nextInt(1000, 2500);
    }
}
