package com.inubot.complete.tutisland.tasks;

import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;
import com.inubot.api.methods.*;

/**
 * Created by Cameron on 2015-04-30.
 */
public class MasterChef extends TutorialIslandTask {

    @Override
    public boolean verify() {
        return Varps.get(281) >= 130 && Varps.get(281) <= 210;
    }

    @Override
    public void run() {
            if (Varps.get(281) == 140) {
                final Npc masterChef = Npcs.getNearest("Master Chef");
                if (masterChef != null) {
                    masterChef.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 130) {
                final GameObject door = GameObjects.getNearest("Door");
                if (door != null) {
                    door.processAction("Open");
                }
            } else if (Varps.get(281) == 150) {
                final WidgetItem flour = Inventory.getFirst("Pot of flour");
                final WidgetItem water = Inventory.getFirst("Bucket of water");
                if (flour != null && water != null) {
                    flour.use(water);
                }
            } else if (Varps.get(281) == 160) {
                final WidgetItem dough = Inventory.getFirst("Bread dough");
                final GameObject range = GameObjects.getNearest("Range");
                if (dough != null && range != null) {
                    dough.use(range);
                }
            } else if (Varps.get(281) == 170) {
                Tabs.open(Tab.MUSIC_PLAYER);
            } else if (Varps.get(281) == 180) {
                final Tile tile = new Tile(3074, 3090, 0);
                Movement.walkTo(tile);
                while (!Players.getLocal().getLocation().equals(tile)) {
                    Time.sleep(300);
                }
                final GameObject door = GameObjects.getNearest("Door");
                if (door != null) {
                    door.processAction("Open");
                    Time.sleep(2500);
                }
            } else if (Varps.get(281) == 183 || Varps.get(281) == 187) {
                int val = Varps.get(281);
                if (Tabs.getOpen() != Tab.EMOTES)
                    Tabs.open(Tab.EMOTES);
                Time.sleep(500);
                Client.processAction(1, 0, 14155777, 57, "Yes", "", 50, 50);
                Time.sleep(3000);
            } else if (Varps.get(281) == 190) {
                Tabs.open(Tab.OPTIONS);
            } else if (Varps.get(281) == 200) {
                Movement.toggleRun(true);
                Time.sleep(700);
            } else if (Varps.get(281) == 210) {
                //Movement.walkTo(new Tile(3070, 3103));
                //Time.sleep(3000);
                final Tile dest = new Tile(3086, 3126);
                Movement.walkTo(dest);
                Time.sleep(500);
                for (int i = 0; i < 10; i++) {
                    if (dest.distance() < 5)
                        break;
                    Time.sleep(1500);
                }
                final GameObject door = GameObjects.getNearest("Door");
                if (door != null) {
                    door.processAction("Open");
                    Time.sleep(2500);
                }
            }
    }
}
