/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.combat;

import com.inubot.api.oldschool.*;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.action.tree.DialogButtonAction;
import com.inubot.api.methods.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author unsigned
 * @since 22-05-2015
 */
public class CombatTrainerPRO extends Script implements Paintable {

    private static final Filter<Widget> DIALOGUE_FILTER = w -> w.getText() != null && w.getText().equals("Click here to continue");
    private static final Skill[] MAJOR_SKILLS = {Skill.RANGED, Skill.DEFENCE};
    private static final Tile SEAGULLS = new Tile(3030, 3236, 0), COWS = new Tile(3031, 3315, 0),
            DRAYNOR_BANK = new Tile(3092, 3245, 0), GATE_TILE = new Tile(3031, 3313, 0), MOSLAMS = new Tile(3294, 3173, 0),
            GOBLINS = new Tile(3259, 3232, 0), KHARID_GATE = new Tile(3266, 3227, 0);
    private static final Area GOBLIN_AREA = new Area(new Tile(3239, 3196, 0), new Tile(3267, 3251, 0)),
            KHARID_AREA = new Area(new Tile(3266, 3231, 0), new Tile(3309, 3154, 0));
    //[att, def, str, hp]
    private final Stat[] stats = new Stat[4];
    //[att, def, str]
    private final StopWatch stopWatch = new StopWatch(0);

    private final Map<String, String> stronghold = new HashMap<String, String>() {{
        super.put("A website claims that they can make me a player moderator if I give them my password. What should I do?", "Don't tell them anything and inform Jagex through the game website.");
        super.put("Can I leave my account logged in while I'm out of the room?", "No.");
        super.put("My friend uses this great add-on program he got from a website, should I?", "No, it might steal my password.");
        super.put("My friend asks me for my password so that he can do a difficult quest for me.", "Don't give him my password.");
        super.put("Recovery answers should be...", "Memorable.");
        super.put("What are your recovery questions used for?", "To recover my account if i don't remember my password.");
        super.put("What is a good example of a Bank PIN?", "The birthday of a famous person or event.");
        super.put("What do you do if someone asks you for your password or recoveries to make you a member for free?", "Don't tell them anything and click the Report Abuse button.");
        super.put("What do you do if someone asks you for your password or recoveries to make you a player moderator?", "Don't tell them anything and click the Report Abuse button.");
        super.put("What do you do if someone tells you that you have won the RuneScape lottery and asks you for your password and recoveries to award your prize?", "Don't tell them anything and click the Report Abuse button.");
        super.put("What do I do if I think I have a keylogger or a virus?", "Virus scan my computer then change my password and recoveries.");
        super.put("What do I do if a moderator asks me for my account details?", "Politely tell them no then use the report abuse button.");
        super.put("What should I do if I think someone knows my recoveries?", "Use the recover a lost password section.");
        super.put("Where can I find cheats for RuneScape?", "Nowhere.");
        super.put("Where should I enter my password for RuneScape?", "Only on the RuneScape or FunOrb website.");
        super.put("Will Jagex block me for saying my PIN ingame?", "No.");
        super.put("Who can I give my password to?", "Nobody.");
        super.put("Why do I need to type in recovery questions?", "To help me recover my password if I forget it or if it is stolen.");
    }};

    private String answerFor(String question) {
        return stronghold.get(question);
    }

    private String answerFor(Predicate<String> pred) {
        for (Map.Entry<String, String> entry : stronghold.entrySet()) {
            if (pred.test(entry.getKey()))
                return entry.getValue();
        }
        return null;
    }

    private Widget solverFor(Predicate<String> pred) {
        String answer = answerFor(pred);
        return answer == null ? null : Interfaces.getWidgetByText(answer);
    }

    private Widget solverFor(String question) {
        String answer = answerFor(question);
        return answer == null ? null : Interfaces.getWidgetByText(answer);
    }

    @Override
    public boolean setup() {
        for (int i = 0; i < 4; i++) {
            Skill skill = Skill.values()[i];
            stats[i] = new Stat(skill, Skills.getExperience(skill));
        }
        return Game.isLoggedIn();
    }

    private void switchStyles() {
        int style = Combat.getStyle();
        int att = Skills.getLevel(Skill.ATTACK), str = Skills.getLevel(Skill.STRENGTH), def = Skills.getLevel(Skill.DEFENCE);
        if (str < att && style != 1) {
            Combat.setStyle(1);
        } else if (def < str && style != 2) { //if you're using no weap or a 3-style weap change this to 3, else 2
            Combat.setStyle(2); //and this
        } else if (style != 0 && att <= str && att <= def) {
            Combat.setStyle(0);
        }
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 600;
        for (Widget widget : Interfaces.getWidgets(DIALOGUE_FILTER))
            Client.processAction(DialogButtonAction.clickHereToContinue(widget.getId()), "Continue", "");
        if (Players.getLocal().getTargetIndex() != -1)
            return 600;
        if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 10) {
            Movement.toggleRun(true);
        }
        switchStyles();
        switch (Target.getCurrent()) {
            case SEAGULL: {
                Npc npc = Npcs.getNearest(n -> n.getName() != null && n.getName().equals("Seagull") && n.getTargetIndex() == -1 && n.getAnimation() == -1);
                if (npc != null) {
                    if (Movement.isObjectReachable(npc))
                        npc.processAction("Attack");
                } else if (SEAGULLS.distance() > 15) {
                    Movement.walkTo(SEAGULLS);
                }
                break;
            }
            case COW: {
                if (Skills.getCurrentLevel(Skill.HITPOINTS) < 6)
                    return 3000;
                /*if (Inventory.isFull()) {
                    if (DRAYNOR_BANK.distance() < 10) {
                        if (!Bank.isOpen()) {
                            GameObject booth = GameObjects.getNearest("Bank booth");
                            if (booth != null) {
                                booth.processAction("Bank");
                                return 2000;
                            }
                        } else {
                            Bank.depositInventory();
                            Bank.close();
                            return 700;
                        }
                    } else {
                        GameObject fence = GameObjects.getNearest(t -> t.getLocation().equals(GATE_TILE));
                        if (fence != null && fence.containsAction("Open")) {
                            fence.processAction("Open");
                            return 1500;
                        }
                        Movement.walkTo(DRAYNOR_BANK);
                        return 2000;
                    }
                }
                if (!Inventory.isFull() && Players.getLocal().getTargetIndex() == -1) {
                    GroundItem item = GroundItems.getNearest("Cowhide");
                    if (item != null) {
                        item.processAction("Take");
                        return 1000;
                    }
                }*/
                Npc npc = Npcs.getNearest(n -> n.getName() != null && (n.getName().equals("Cow") || n.getName().equals("Cow calf")) && n.getTargetIndex() == -1 && n.getAnimation() == -1);
                if (npc != null) {
                    if (Movement.isObjectReachable(npc)) {
                        npc.processAction("Attack");
                    } else {
                        GameObject fence = GameObjects.getNearest(t -> t.getLocation().equals(GATE_TILE));
                        if (fence != null && fence.containsAction("Open")) {
                            fence.processAction("Open");
                            return 1500;
                        }
                    }
                } else if (COWS.distance() > 13) {
                    Movement.walkTo(COWS);
                }
                break;
            }
            case MOSLAM: {
                if (Skills.getCurrentLevel(Skill.HITPOINTS) < 10)
                    return 3000;
                Npc npc = Npcs.getNearest(n -> n.getName() != null && n.getName().equals("Al-Kharid warrior") && n.getAnimation() == -1);
                if (npc != null) {
                    if (Movement.isReachable(npc)) {
                        npc.processAction("Attack");
                    } else {
                        GameObject door = GameObjects.getNearest(t -> t.getName() != null && t.getName().equals("Large door") && t.containsAction("Open"));
                        if (door != null) {
                            door.processAction("Open");
                            return 1500;
                        }
                    }
                } else if (MOSLAMS.distance() > 13) {
                    if (COWS.distance() < 20) {
                        Time.sleep(10000);
                        Client.processAction(1, -1, 14286849, 57, "Cast", "Lumbridge Home Teleport", 50, 50);
                        return 5000;
                    }
                    if (GOBLIN_AREA.contains(Players.getLocal()) && Inventory.getFirst("Coins") != null && Inventory.getFirst("Coins").getQuantity() >= 10) {
                        if (KHARID_GATE.distance() > 1) {
                            Movement.walkTo(KHARID_GATE);
                        } else {
                            GameObject obj = GameObjects.getNearest(t -> t.getName() != null && t.getName().equals("Gate") && t.containsAction("Pay-toll(10gp)"));
                            if (obj != null) {
                                obj.processAction("Pay-toll(10gp)");
                                return 2000;
                            }
                        }
                    } else if (!KHARID_AREA.contains(Players.getLocal()) && GOBLINS.distance() > 20) {
                        Movement.walkTo(GOBLINS);
                        return 2000;
                    } else {
                        GroundItem coins = GroundItems.getNearest("Coins");
                        if (coins != null && Movement.isObjectReachable(coins)) {
                            coins.processAction("Take");
                            return 2000;
                        }
                        Npc goblin = Npcs.getNearest(n -> n.getName() != null && n.getName().equals("Goblin") && n.getTargetIndex() == -1 && n.getAnimation() == -1);
                        if (goblin != null) {
                            if (Movement.isObjectReachable(goblin))
                                goblin.processAction("Attack");
                        }
                    }
                    Movement.walkTo(MOSLAMS);
                    return 2000;
                }
                break;
            }
        }
        return 500;
    }

    @Override
    public void render(Graphics2D g) {
        int y = 20;
        AWTUtil.drawBoldedString(g, "Runtime: " + stopWatch.toElapsedString(), 20, 20, Color.MAGENTA);
        for (Stat stat : stats) {
            int gain = Skills.getExperience(stat.getSkill()) - stat.getStartExp();
            AWTUtil.drawBoldedString(g, "" + stat.toString().toLowerCase() + ": " + gain, 20, y += 20, Color.YELLOW);
        }
    }

    private enum Target {

        SEAGULL(20), //20-20-20
        COW(70),     //until 45-45-45
        MOSLAM(420);

        private final int max;

        private Target(int max) {
            this.max = max;
        }

        private static Target getCurrent() {
            for (Target target : values()) {
                for (Skill skill : MAJOR_SKILLS) {
                    if (Skills.getLevel(skill) < target.getMax())
                        return target;
                }
            }
            return Target.MOSLAM;
        }

        public int getMax() {
            return max;
        }
    }

    private class Stat {

        private final Skill skill;
        private final int startExp;

        private Stat(Skill skill, int startExp) {
            this.skill = skill;
            this.startExp = startExp;
        }

        public Skill getSkill() {
            return skill;
        }

        public int getStartExp() {
            return startExp;
        }

        @Override
        public String toString() {
            return skill.toString().toLowerCase();
        }
    }
}
