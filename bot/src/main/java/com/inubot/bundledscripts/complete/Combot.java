package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.Character;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

/**
 * @author Septron
 * @since June 20, 2015
 */
@Manifest(name = "Combot", developer = "Septron", desc = "Fights various monsters and switches combat styles", version = 1.0)
public class Combot extends ProScript {

    private static final Tile GATE_TILE = new Tile(3031, 3313, 0);
    private boolean bones = false;

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    private void switchStyles() {
        switch (Combat.getStyle()) {
            case 0:
                if (Skills.getLevel(Skill.ATTACK) > Skills.getLevel(Skill.STRENGTH))
                    Combat.setStyle(1);
                break;
            case 1:
                if (Skills.getLevel(Skill.STRENGTH) > Skills.getLevel(Skill.DEFENCE))
                    Combat.setStyle(2);
                break;
            case 2:
            case 3:
                if (Skills.getLevel(Skill.DEFENCE) >= Skills.getLevel(Skill.ATTACK))
                    Combat.setStyle(0);
                break;
        }
    }

    public Monster getCurrent() {
        for (Monster monster : Monster.values()) {
            if (Skills.getLevel(Skill.DEFENCE) <= monster.max)
                return monster;
        }
        return Monster.SEAGULL;
    }

    private boolean attack(Monster monster) {
        Npc npc = Npcs.getNearest(target -> target.getTarget() == null && !target.isHealthBarVisible()
                && Movement.isReachable(target) && target.getName() != null
                && target.getName().toLowerCase().equals(monster.name().toLowerCase()));
        if (npc != null) {
            npc.processAction("Attack");
            return true;
        }
        return false;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn()) {
            return 1500;
        }
        if (Interfaces.canContinue()) {
            Mouse.click(298, 446);
        }
        switchStyles();

        Character character = Players.getLocal().getTarget();
        if (character != null && character instanceof Npc) {
            Npc npc = (Npc) character;
            System.out.println(npc.getHealthPercent());
            if (npc.isHealthBarVisible())
                System.out.println("hp bar vis");
            else
                System.out.println("no hp bar");
        }

        if (Players.getLocal().getTarget() != null) {
            return 600;
        }

        if (Movement.getRunEnergy() > 10 && !Movement.isRunEnabled()) {
            Movement.toggleRun(true);
        }

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
                if (Players.getLocal().getTarget() == null || Players.getLocal().getTarget().getTarget() == null) {
                    if (!attack(monster) && monster.tile.distance() > 15) {
                        Movement.walkTo(monster.tile);
                    }
                }
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

    private enum Monster {
        SEAGULL(new Tile(3030, 3236), 25),
        //GOBLIN      (new Tile(3183, 3246), 25),
        COWS(new Tile(3031, 3315), 99);
        //MONK        (new Tile(0, 0), 99);

        private final Tile tile;
        private final int max;

        Monster(Tile tile, int max) {
            this.tile = tile;
            this.max = max;
        }
    }
}
