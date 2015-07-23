package com.inubot.complete;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.script.Manifest;
import com.inubot.proframework.ProScript;

import java.util.Map;

/**
 * @author Septron
 * @since June 20, 2015
 */
@Manifest(name = "Combot", developer = "Septron", desc = "Fights various monsters and switches combat styles", version = 1.0)
public class Combot extends ProScript {

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    private enum Monster {
        SEAGULL     (new Tile(3030, 3236), 25),
        //GOBLIN      (new Tile(3183, 3246), 25),
        COWS        (new Tile(3031, 3315), 99);
        //MONK        (new Tile(0, 0), 99);

        private final Tile tile;
        private final int max;

        Monster(Tile tile, int max) {
            this.tile = tile;
            this.max = max;
        }
    }

    private static Tile GATE_TILE = new Tile(3031, 3313, 0);

    private boolean bones = false;

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
                break;
            case 2:
            case 3:
                if (skills[2] >= skills[0])
                    Combat.setStyle(0);
                break;
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

    public Monster getCurrent() {
        for (Monster monster : Monster.values()) {
            if (getLowestSkillLevel() < monster.max)
                return monster;
        }
        return Monster.SEAGULL;
    }

    public boolean attack(Monster monster) {
        Npc npc = Npcs.getNearest(target -> target.getTarget() == null && !target.isHealthBarVisible() && Movement.isReachable(target)
                    && target.getName().toLowerCase().equals(monster.name().toLowerCase()));
        if (npc != null) {
            npc.processAction("Attack");
            return true;
        }
        return false;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 1500;

        if (Interfaces.canContinue())
            Interfaces.processContinue();
        switchStyles();

        if (Players.getLocal().getTarget() != null) {
            return 600;
        }

        if (Movement.getRunEnergy() > 10 && !Movement.isRunEnabled())
            Movement.toggleRun(true);

        if (bones) {
            GroundItem gi = GroundItems.getNearest(a -> a.getName().toLowerCase().contains("bone") && a.distance() < 8);
            if (gi != null && Players.getLocal().getAnimation() == -1) {
                gi.processAction("Take");
                return 1000;
            }

            WidgetItem ii = Inventory.getFirst(a -> a.getName().toLowerCase().contains("bone"));
            if (ii != null) {
                ii.processAction("Bury");
                return 600;
            }
        }

        Monster monster = getCurrent();
        switch (monster) {
            case SEAGULL:
                if (Players.getLocal().getTarget() == null || Players.getLocal().getTarget().getTarget() == null)
                    if (!attack(monster))
                        if (monster.tile.distance() > 15)
                            Movement.walkTo(monster.tile);
                break;
            case COWS:
                Npc npc = Npcs.getNearest(n -> n.getName() != null && (n.getName().equals("Cow") || n.getName().equals("Cow calf")) && n.getTargetIndex() == -1 && n.getAnimation() == -1);
                if (npc != null) {
                    if (Movement.isReachable(npc)) {
                        npc.processAction("Attack");
                    } else {
                        GameObject fence = GameObjects.getNearest(t -> t.getLocation().equals(GATE_TILE));
                        if (fence != null && fence.containsAction("Open")) {
                            fence.processAction("Open");
                            return 1500;
                        }
                    }
                } else if (monster.tile.distance() > 13) {
                    Movement.walkTo(monster.tile);
                }
        }
        return 600;
    }
}
