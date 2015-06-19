package org.runedream.script.memes.range;

import org.runedream.RuneDream;
import org.runedream.api.methods.*;
import org.runedream.api.oldschool.Skill;
import org.runedream.api.oldschool.Widget;
import org.runedream.api.util.*;
import org.runedream.api.util.filter.Filter;
import org.runedream.script.Script;
import org.runedream.script.memes.Action;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

/**
 * Created by Cameron on 2015-04-24.
 */
public class RangeGuild extends Script implements Paintable {

    public static final Filter<Widget> DIALOGUE_FILTER = w -> w.getText() != null && (w.getText().equals("Click here to continue") || w.getText().equals("Sure, I'll give it a go."));
    public static final Filter<Widget> LOBBY_FILTER = w -> w.getText() != null && w.getText().equals("Play RuneScape");

    private final Action[] tasks = {new LobbyHandler(), new DialogueHandler(), new EquipArrows(), new TalkToJudge(),
            new ShootArrows()};

    private int startingRangeExperience;
    private long startingTime;

    public boolean setup() {
        startingRangeExperience = Skills.getExperience(Skill.RANGED);
        startingTime = System.currentTimeMillis();
        return true;
    }

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

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 300;
        RuneDream.getInstance().getCanvas().pressKey(KeyEvent.VK_RIGHT, 200);
        RuneDream.getInstance().getCanvas().releaseKey(KeyEvent.VK_RIGHT);
        for (Action task : tasks) {
            if (task.validate()) {
                task.execute();
                Time.sleep(300, 600);
                break;
            }
        }
        return 200;
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
