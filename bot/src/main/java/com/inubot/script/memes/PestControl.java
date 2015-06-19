package com.inubot.script.memes;

import com.inubot.api.methods.*;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.Time;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;
import com.inubot.api.oldschool.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Family on 5/19/2015.
 */
public class PestControl extends Script implements Paintable {

    private static final Area BOAT = new Area(new Tile(2660, 2639, 0), new Tile(2663, 2643, 0));
    private static final Filter<Npc> ATTACKABLE = npc -> npc.getName() != null && !npc.isDying()
            && (npc.getName().equals("Brawler") || npc.getName().equals("Defiler")
            || npc.getName().equals("Ravager") || npc.getName().equals("Shifter")
            || npc.getName().equals("Spinner") || npc.getName().equals("Splatter")
            || npc.getName().equals("Torcher"));
    private static final Color MAIN_BACKGROUND_COLOUR = new Color(0, 102, 204, 149);
    private static final Color MAIN_STROKE_COLOUR = new Color(0, 102, 204, 205);
    private static final Color FONT_COLOUR = new Color(255, 255, 255);
    private static final Color SKILL_BAR_COLOUR = new Color(0, 0, 0, 130);
    private static final Color SKILL_BAR_STROKE = new Color(0, 0, 0, 210);
    private static final Color SKILL_ATTACK = new Color(255, 24, 0, 130);
    private static final Color SKILL_STRENGTH = new Color(0, 255, 1, 130);
    private static final Color SKILL_DEFENCE = new Color(0, 2, 255, 130);
    private static final Color SKILL_HITPOINTS = new Color(230, 69, 0, 130);
    private static final BasicStroke STROKE = new BasicStroke(1);
    private static final Font ARIAL = new Font("Arial", 0, 12);
    private final StopWatch runtime = new StopWatch(0);
    private int startingAttackExperience, startingStrengthExperience, startingDefenceExperience, startingHitpointsExperience;

    public static ScriptStage getStage() {
        Player me = Players.getLocal();
        if (me.getLocation().getX() == 2657) {
            return ScriptStage.JOINING;
        } else if (BOAT.contains(me)) {
            return ScriptStage.WAITING;
        } else {
            if (Npcs.getNearest(ATTACKABLE) != null) {
                return ScriptStage.ATTACKING_NPC;
            } else {
                return ScriptStage.MOVING;
            }
        }
    }

    private static Tile safeTileFor(Tile src, Tile bad) {
        return src.getX() < bad.getX() ? src.derive(-1, 0) : src.derive(1, 0);
    }

    public boolean setup() {
        startingAttackExperience = Skills.getExperience(Skill.ATTACK);
        startingStrengthExperience = Skills.getExperience(Skill.STRENGTH);
        startingDefenceExperience = Skills.getExperience(Skill.DEFENCE);
        startingHitpointsExperience = Skills.getExperience(Skill.HITPOINTS);
        return true;
    }

    @Override
    public int loop() {
        switchStyle();
        if (Players.getLocal().getTargetIndex() != -1) {
            com.inubot.api.oldschool.Character<?> target = Players.getLocal().getTarget();
            if (target.isDying() && "Splatter".equals(target.getName())) {
                Movement.walkTo(safeTileFor(Players.getLocal().getLocation(), target.getLocation()));
            }
        }
        switch (getStage()) {
            case JOINING:
                final GameObject cross = GameObjects.getNearest("Gangplank");
                if (cross != null)
                    cross.processAction("Cross");
                break;
            case WAITING:
                Time.sleep(1500);
                break;
            case ATTACKING_NPC:
                if (Players.getLocal().getTargetIndex() == -1) {
                    final Npc pest = Npcs.getNearest(ATTACKABLE);
                    if (pest != null) {
                        pest.processAction("Attack");
                    }
                }
                break;
            case MOVING:
                final Npc voidKnight = Npcs.getNearest("Void Knight");
                if (voidKnight != null) {
                    Movement.walkTo(voidKnight.getLocation());
                } else {
                    Movement.walkTo(new Tile(Players.getLocal().getX(), Players.getLocal().getY() - 16));
                }
                break;
        }
        return 400;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(MAIN_BACKGROUND_COLOUR);
        g.fillRect(17, 13, 123, 50);
        g.setColor(MAIN_STROKE_COLOUR);
        g.setStroke(STROKE);
        g.drawRect(17, 13, 123, 50);

        g.setColor(SKILL_BAR_COLOUR);
        g.fillRect(7, 345, 489, 21);
        g.setColor(SKILL_BAR_STROKE);
        g.drawRect(7, 345, 489, 21);
        g.setColor(SKILL_BAR_COLOUR);
        g.fillRect(7, 366, 489, 21);
        g.setColor(SKILL_BAR_STROKE);
        g.drawRect(7, 366, 489, 21);
        g.setColor(SKILL_BAR_COLOUR);
        g.fillRect(7, 387, 489, 21);
        g.setColor(SKILL_BAR_STROKE);
        g.drawRect(7, 387, 489, 21);
        g.setColor(SKILL_BAR_COLOUR);
        g.fillRect(7, 408, 489, 21);
        g.setColor(SKILL_BAR_STROKE);
        g.drawRect(7, 408, 489, 21);

        g.setColor(SKILL_ATTACK);
        g.fillRect(8, 346, ((int) 4.87 * getPercentToNextLevel(Skill.ATTACK)), 20);
        g.setColor(SKILL_STRENGTH);
        g.fillRect(8, 367, ((int) 4.87 * getPercentToNextLevel(Skill.STRENGTH)), 20);
        g.setColor(SKILL_DEFENCE);
        g.fillRect(8, 388, ((int) 4.87 * getPercentToNextLevel(Skill.DEFENCE)), 20);
        g.setColor(SKILL_HITPOINTS);
        g.fillRect(8, 409, ((int) 4.87 * getPercentToNextLevel(Skill.HITPOINTS)), 20);//testff

        g.setColor(FONT_COLOUR);
        g.setFont(ARIAL);
        g.drawString("AutoPestControl", 29, 35);
        g.drawString("Runtime: " + runtime.toElapsedString(), 29, 51);
        g.drawString("Attack: " + Skills.getCurrentLevel(Skill.ATTACK) + "/" + Skills.getLevel(Skill.ATTACK) + " | XP: " + format(getExperienceGained(Skill.ATTACK)) + " | Hourly XP: " + format(perHour(getExperienceGained(Skill.ATTACK))) + " | TTL: " + getTNL(Skill.ATTACK, perHour(getExperienceGained(Skill.ATTACK))) + " | Percent: " + getPercentToNextLevel(Skill.ATTACK) + "%", 19, 361);
        g.drawString("Strength: " + Skills.getCurrentLevel(Skill.STRENGTH) + "/" + Skills.getLevel(Skill.STRENGTH) + " | XP: " + format(getExperienceGained(Skill.STRENGTH)) + " | Hourly XP: " + format(perHour(getExperienceGained(Skill.STRENGTH))) + " | TTL: " + getTNL(Skill.STRENGTH, perHour(getExperienceGained(Skill.STRENGTH))) + " | Percent: " + getPercentToNextLevel(Skill.STRENGTH) + "%", 19, 382);
        g.drawString("Defence: " + Skills.getCurrentLevel(Skill.DEFENCE) + "/" + Skills.getLevel(Skill.DEFENCE) + " | XP: " + format(getExperienceGained(Skill.DEFENCE)) + " | Hourly XP: " + format(perHour(getExperienceGained(Skill.DEFENCE))) + " | TTL: " + getTNL(Skill.DEFENCE, perHour(getExperienceGained(Skill.DEFENCE))) + " | Percent: " + getPercentToNextLevel(Skill.DEFENCE) + "%", 19, 403);
        g.drawString("Hitpoints: " + Skills.getCurrentLevel(Skill.HITPOINTS) + "/" + Skills.getLevel(Skill.HITPOINTS) + " | XP: " + format(getExperienceGained(Skill.HITPOINTS)) + " | Hourly XP: " + format(perHour(getExperienceGained(Skill.HITPOINTS))) + " | TTL: " + getTNL(Skill.HITPOINTS, perHour(getExperienceGained(Skill.HITPOINTS))) + " | Percent: " + getPercentToNextLevel(Skill.HITPOINTS) + "%", 19, 424);
    }

    public void switchStyle() {
        if (Skills.getLevel(Skill.ATTACK) - Skills.getLevel(Skill.STRENGTH) >= 1) {
            if (Combat.getStyle() == 0) {
                Combat.setStyle(1);
            }
        } else if (Skills.getLevel(Skill.STRENGTH) - Skills.getLevel(Skill.DEFENCE) >= 1) {
            if (Combat.getStyle() == 1) {
                Combat.setStyle(3); //2 if 4 style weapon
            }
        } else if (Skills.getLevel(Skill.DEFENCE) - Skills.getLevel(Skill.ATTACK) >= 1) {
            if (Combat.getStyle() == 3) { //2 if 4 style weapon
                Combat.setStyle(0);
            }
        }
    }

    public String format(int i) {
        return NumberFormat.getNumberInstance(Locale.US).format(i);
    }

    public String getTNL(Skill skill, int hourlyRate) {
        double gain = getExperienceGained(skill);
        if (gain == 0 || Skills.getLevel(skill) == 99)
            return "00:00:00";
        return StopWatch.format((long) (Skills.getExperienceToNextLevel(skill) / hourlyRate * 3600000D));
    }

    private String runtime(long i) {
        DecimalFormat nf = new DecimalFormat("00");
        long millis = System.currentTimeMillis() - i;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;
        return nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds);
    }

    private int getPercentToNextLevel(Skill skill) {
        int lvl = Skills.getLevel(skill);
        if (lvl == 99) {
            return 100;
        }
        int xpTotal = Skills.XP_TABLE[lvl + 1] - Skills.XP_TABLE[lvl];
        if (xpTotal == 0) {
            return 0;
        }
        int xpDone = Skills.getExperience(skill) - Skills.XP_TABLE[lvl];
        return 100 * xpDone / xpTotal;
    }

    public int getExperienceGained(Skill skill) {
        switch (skill.name()) {
            case "ATTACK":
                return Skills.getExperience(skill) - startingAttackExperience;
            case "STRENGTH":
                return Skills.getExperience(skill) - startingStrengthExperience;
            case "DEFENCE":
                return Skills.getExperience(skill) - startingDefenceExperience;
            case "HITPOINTS":
                return Skills.getExperience(skill) - startingHitpointsExperience;
            default:
                return -1;
        }
    }

    public int perHour(int i) {
        return (int) ((i) * 3600000D / (System.currentTimeMillis() - runtime.getStart()));
    }

    public enum ScriptStage {

        JOINING,
        WAITING,
        ATTACKING_NPC,
        MOVING

    }
}