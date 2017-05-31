package com.inubot.bundledscripts.complete.rangeguild;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Skills;
import com.inubot.api.methods.Varps;
import com.inubot.api.oldschool.InterfaceComponent;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created by Cameron on 2015-04-24.
 */
@Manifest(
        name = "RangeGuild PRO",
        developer = "bone",
        desc = "Plays the range guild minigame for up to 50k experience per hour!"
)
public class RangeGuild extends Script implements Paintable {

    static final Filter<InterfaceComponent> DIALOGUE_FILTER = w -> w.getText() != null && (w.getText().equals("Click here to continue") || w.getText().equals("Sure, I'll give it a go."));
    private final Action[] tasks = {new EquipArrows(), new DialogueHandler(), new TalkToJudge(), new ShootArrows()};

    private int startingRangeExperience;
    private long startingTime;

    public static int getShotsFired() {
        int var = Varps.get(156);
        return var > 0 ? var - 1 : -1;
    }

    public static int getCurrentScore() {
        int var = Varps.get(157);
        return var > 0 ? var - 1 : -1;
    }

    public static boolean isGameFinished() {
        return getShotsFired() == 10;
    }

    public static boolean isGameStarted() {
        return getShotsFired() != -1;
    }

    public boolean setup() {
        startingRangeExperience = Skills.getExperience(Skill.RANGED);
        startingTime = System.currentTimeMillis();
        return true;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 300;
        for (Action task : tasks) {
            if (task.validate()) {
                task.execute();
                break;
            }
        }
        return 1000;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "AutoRangeGuild PRO", 20, 40, Color.MAGENTA);
        AWTUtil.drawBoldedString(g, "Runtime: " + runtime(startingTime), 20, 60, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Range Experience: " + formatNumber((Skills.getExperience(Skill.RANGED) - startingRangeExperience)) + "(" + perHour(Skills.getExperience(Skill.RANGED) - startingRangeExperience) + ")", 20, 80, Color.YELLOW);
    }

    //thanks kenneh
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

    private String perHour(int gained) {
        return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startingTime)));
    }

    private String formatNumber(int start) {
        DecimalFormat nf = new DecimalFormat("0.0");
        if (start >= 1000000) {
            return nf.format(((double) start / 1000000)) + "m";
        }
        if (start >= 1000) {
            return nf.format(((double) start / 1000)) + "k";
        }
        return String.valueOf(start);
    }
}
