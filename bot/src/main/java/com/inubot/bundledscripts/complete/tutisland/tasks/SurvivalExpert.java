package com.inubot.bundledscripts.complete.tutisland.tasks;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;

/**
 * Created by Cameron on 2015-04-30.
 */
public class SurvivalExpert extends TutorialIslandTask {

    @Override
    public boolean verify() {
        return Varps.get(281) >= 20 && Varps.get(281) <= 120;
    }

    @Override
    public void run() {
            if (Varps.get(281) == 20 || Varps.get(281) == 70) {
                final Npc survivalExpert = Npcs.getNearest("Survival Expert");
                if (survivalExpert != null) {
                    survivalExpert.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 30) {
                Tabs.open(Tab.INVENTORY);
            } else if (Varps.get(281) == 40) {
                final GameObject tree = GameObjects.getNearest("Tree");
                if (tree != null && Players.getLocal().getAnimation() == -1) {
                    tree.processAction("Chop down");
                }
            } else if (Varps.get(281) == 50) {
                final Player local = Players.getLocal();
                final GameObject[] standing = GameObjects.getLoadedAt(local.getRegionX(), local.getRegionY(), Game.getPlane());
                if (standing != null && standing.length > 0) {
                    final Area area = new Area(new Tile(local.getX() + 5, local.getY() + 5), new Tile(local.getX() - 5, local.getY() - 5));
                    for (Tile tile : area.getTiles()) {
                        final GameObject[] objects = GameObjects.getLoadedAt(local.getRegionX(), local.getRegionY(), Game.getPlane());
                        if (objects != null && standing.length > 0) continue;
                        Movement.walkTo(tile);
                        break;
                    }
                }
                final WidgetItem tinderbox = Inventory.getFirst("Tinderbox");
                final WidgetItem logs = Inventory.getFirst("Logs");
                if (tinderbox != null && logs != null) {
                    tinderbox.use(logs);
                    Time.sleep(500);
                }
            } else if (Varps.get(281) == 60) {
                Tabs.open(Tab.STATS);
            } else if (Varps.get(281) == 80) {
                final Npc fishingSpot = Npcs.getNearest("Fishing spot");
                if (fishingSpot != null) {
                    while (Inventory.getItems(wi -> wi.getName().equals("Raw shrimps")).length < 2) {
                        if (Players.getLocal().getAnimation() == -1)
                            fishingSpot.processAction("Net");
                        Time.sleep(500);
                    }
                }
            } else if (Varps.get(281) == 90 || Varps.get(281) == 110) {
                final GameObject fire = GameObjects.getNearest("Fire");
                final WidgetItem shrimp = Inventory.getFirst("Raw shrimps");
                if (shrimp != null && fire != null) {
                    shrimp.use(fire);
                    Time.sleep(2000);
                }
            } else if (Varps.get(281) == 120) {
                final GameObject gate = GameObjects.getNearest("Gate");
                if (gate != null) {
                    gate.processAction("Open");
                    Time.sleep(500);
                }
            }
    }
}
