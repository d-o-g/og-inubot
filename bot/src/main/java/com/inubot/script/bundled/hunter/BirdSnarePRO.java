package com.inubot.script.bundled.hunter;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Time;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Script;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created by Cameron on 2015-04-27.
 * <p>
 * TODO find best Formations
 * 1 trap
 * X
 * ---------------------------------------------------------------------------------------------------------
 * 2 trap
 * X X
 * ---------------------------------------------------------------------------------------------------------
 * 3 trap
 * X X
 * X
 * ---------------------------------------------------------------------------------------------------------
 */
public class BirdSnarePRO extends Script implements Paintable {

    private Tile tile = null;
    private int startExp = 0;
    private long startTime;

    private int getMaxTraps() {
        return Skills.getLevel(Skill.HUNTER) / 20 + 1;
    }

    public boolean setup() {
        /*if (!Game.isLoggedIn()) {
            System.err.println("Start logged in!"); //need 2 get base tile. cud do something like setup(String... args) and hardcode start tile
            return false;
        }*/
        startExp = Skills.getExperience(Skill.HUNTER);
        startTime = System.currentTimeMillis();
        return true;
    }

    @Override
    public int loop() {
        if (Game.isLoggedIn()) {
            if (tile == null) {
                tile = Players.getLocal().getLocation();
                System.out.println("Start tile set to: " + tile.toString());
            }
        }
        if (Players.getLocal().getAnimation() != -1) {
            return 400;
        }
        if (Inventory.getCount() > 25)
            Inventory.dropAll(item -> (item.getName().equals("Raw bird meat") || item.getName().equals("Bones")));
        Tile next = getNextLocation();
        if (next != null) {
            GroundItem gi = GroundItems.getNearest(t -> t.getLocation().equals(next) && "Bird snare".equals(t.getName()));
            GameObject obj = GameObjects.getNearest(t -> t.getLocation().equals(next));
            if (obj != null && (obj.containsAction("Check") || !obj.containsAction("Investigate"))) {
                obj.processAction(obj.containsAction("Check") ? "Check" : "Dismantle");
                Time.await(() -> GameObjects.getNearest(t -> t.getLocation().equals(next)) == null
                        && Players.getLocal().getAnimation() == -1, 1500);
            } else if (gi != null) {
                gi.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
                Time.await(() -> GameObjects.getNearest(t -> t.getLocation().equals(next)) != null
                        && Players.getLocal().getAnimation() == -1, 1500);
            }
            if (gi == null && obj == null && Players.getLocal().getAnimation() == -1) {
                if (!Players.getLocal().getLocation().equals(next)) {
                    Movement.walkTo(next);
                    Time.await(() -> Players.getLocal().getLocation().equals(next), 1500);
                }
                WidgetItem snare = Inventory.getFirst("Bird snare");
                if (snare != null && Players.getLocal().getLocation().equals(next)) {
                    snare.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
                    Time.sleep(300, 400);
                    if (Time.await(() -> Players.getLocal().getAnimation() == -1, 1500)) {
                        Movement.walkTo(next.derive(0, 1));
                    }
                }
            }
        }
        return 500;
    }

    private Tile[] getTrapTactics() {
        switch (getMaxTraps()) {
            case 1:
                return new Tile[]{tile};
            case 2:
                return new Tile[]{tile.derive(-1, 0), tile.derive(1, 0)};
            case 3:
                return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0)};
            case 4:
                return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0), tile.derive(0, 1)};
            case 5:
                return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0), tile.derive(0, 1), tile};
            default: {
                return new Tile[0];
            }
        }
    }

    public Tile getNextLocation() {
        Tile[] formation = getTrapTactics();
        for (final Tile tile : formation) { //no trap is available
            if (GameObjects.getNearest(o -> o.getLocation().equals(tile)) == null)
                return tile;
        }
        for (Tile tile : formation) {
            GameObject obj = GameObjects.getNearest(o -> o.getLocation().equals(tile));
            if (obj != null && (obj.containsAction("Check") || !obj.containsAction("Investigate"))) {
                return tile;
            }
        }
        return null;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "AutoHunter PRO", 20, 20, Color.MAGENTA);
        AWTUtil.drawBoldedString(g, "Runtime: " + runtime(startTime), 20, 40, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Hunting Experience: " + formatNumber((Skills.getExperience(Skill.HUNTER) - startExp)) + "(" + perHour(Skills.getExperience(Skill.HUNTER) - startExp) + ")", 20, 60, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Hunting: Crimson Swift", 20, 80, Color.RED);
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
        return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - startTime)));
    }

    private String formatNumber(int start) {
        DecimalFormat nf = new DecimalFormat("0.0");
        return start >= 1000000 ? nf.format(((double) start / 1000000)) + "m" : start >= 1000 ? nf.format(((double) start / 1000)) + "k" : String.valueOf(start);
    }
}
