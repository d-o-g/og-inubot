package com.inubot.script.others.test.combat;

import com.inubot.api.methods.Combat;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.Tile;
import com.inubot.script.Script;

/**
 * @author Septron
 * @since June 20, 2015
 */
public class Combot extends Script {

    private enum Monster {
        PIGEONS     (new Tile(3030, 3236), 10),
        GOBLINS     (new Tile(0, 0), 20),
        COWS        (new Tile(0, 0), 40);
        // ???

        public final Tile tile;
        public final int max;

        Monster(Tile tile, int max) {
            this.tile = tile;
            this.max = max;
        }
    }

    private int[] getMeleeSkills() {
        return new int[] {
                Skills.getLevel(Skill.ATTACK),
                Skills.getLevel(Skill.STRENGTH),
                Skills.getLevel(Skill.DEFENCE)
        };
    }

    private void switchStyles() {
        final int[] skills = getMeleeSkills();
        switch (Combat.getStyle()) {
            case 0:
                if (skills[0] > skills[1])
                    Combat.setStyle(1);
                break;
            case 1:
                if (skills[1] > skills[2])
                    Combat.setStyle(2);
            case 2:
                if (skills[2] > skills[0])
                    Combat.setStyle(0);
        }
    }

    public int getLowestSkillLevel() {
        final int[] skills = getMeleeSkills();

        int lowest = skills[0];
        for (int i = 1; i < skills.length; i++) {
            if (lowest < skills[i])
                lowest = skills[i];
        }

        return lowest;
    }

    @Override
    public int loop() {

        return 0;
    }
}
