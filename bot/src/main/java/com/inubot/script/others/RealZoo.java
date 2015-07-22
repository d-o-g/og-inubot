package com.inubot.script.others;

import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;

import java.awt.*;

/**
 * Created by bytehound on 7/16/2015.
 */
public class RealZoo extends Script implements Paintable {

    private int startXp;

    @Override
    public boolean setup() {
        startXp = Skills.getExperience(Skill.MAGIC);
        return true;
    }

    @Override
    public int loop() {
        if (Interfaces.canContinue()) {
            Interfaces.processContinue();
            return 1700;
        }
        if (Players.getLocal().getTarget() == null && Players.getLocal().getAnimation() == -1) {
            final Npc zooAnimal = Npcs.getNearest(npc -> npc.getName().equals("Cyclops") || npc.getName().equals("Jogre") || npc.getName().equals("Wolf"));
            if (zooAnimal != null) {
                zooAnimal.processAction("Attack");
            }
        }
        return 300;
    }

    @Override
    public void render(Graphics2D g) {
        int ga = Skills.getExperience(Skill.MAGIC) - startXp;
        AWTUtil.drawBoldedString(g, "Experience: " + ga, 20, 20, Color.MAGENTA);
    }
}
