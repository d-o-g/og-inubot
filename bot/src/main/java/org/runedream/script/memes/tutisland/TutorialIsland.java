package org.runedream.script.memes.tutisland;

import org.runedream.api.methods.Game;
import org.runedream.api.methods.Varps;
import org.runedream.api.util.AWTUtil;
import org.runedream.api.util.Paintable;
import org.runedream.script.Script;
import org.runedream.script.memes.tutisland.tasks.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luckruns0ut on 29/04/15.
 *
 * Known bugs:
 * Lighting a fire doesnt work if one is lit under you.
 * If the script fails to do one thing, it'll stand there doing nothing.
 */
public class TutorialIsland extends Script implements Paintable {
    private List<TutorialIslandTask> tasks = new ArrayList<>();

    @Override
    public boolean setup() {
        tasks.add(new BankArea());
        tasks.add(new CombatInstructor());
        tasks.add(new CharacterCustomizer());
        tasks.add(new MagicInstructor());
        tasks.add(new MasterChef());
        tasks.add(new MiningInstructor());
        tasks.add(new QuestGuide());
        tasks.add(new RuneScapeGuide());
        tasks.add(new SurvivalExpert());
        return true;
    }

    @Override
    public int loop() {
        if(Game.isLoggedIn()) {
            for (TutorialIslandTask task : tasks) {
                if (task.verify()) {
                    task.run();
                    return 500;
                }
            }
        }
        return 500;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "Varp 281: " + Varps.get(281), 20, 20, Color.RED);
    }
}
