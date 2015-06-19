package org.runedream.script.memes.tutisland.tasks;

import org.runedream.api.methods.*;
import org.runedream.api.methods.traversal.Movement;
import org.runedream.api.oldschool.*;
import org.runedream.api.util.Time;
import org.runedream.api.util.filter.Filter;
import org.runedream.api.util.filter.NameFilter;

/**
 * Created by Cameron on 2015-04-30.
 */
public class MiningInstructor extends TutorialIslandTask {

    @Override
    public boolean verify() {
        return Varps.get(281) >= 260 && Varps.get(281) <= 360;
    }

    @Override
    public void run() {
        if (Interfaces.canContinue()) {
            Interfaces.clickContinue();
        } else {
            if (Varps.get(281) == 260 || Varps.get(281) == 290 || Varps.get(281) == 330) {
                final Npc miningInstructor = Npcs.getNearest("Mining Instructor");
                if (miningInstructor != null) {
                    miningInstructor.processAction("Talk-to");
                } else {
                    //only npcs on the minimap are loaded so walk south to make the nigger load
                    Movement.walkTo(Players.getLocal().getLocation().derive(0, -10));
                }
            } else if (Varps.get(281) == 270) {
                Movement.walkTo(new Tile(3078, 9504));
                Time.sleep(2000);
                final GameObject rocks = GameObjects.getNearest("Rocks");
                if (rocks != null) {
                    rocks.processAction("Prospect");
                    Time.sleep(3000);
                }
            } else if (Varps.get(281) == 280) {
                Movement.walkTo(new Tile(3082, 9501));
                Time.sleep(2000);
                final GameObject rocks = GameObjects.getNearest("Rocks");
                if (rocks != null) {
                    rocks.processAction("Prospect");
                }
            } else if (Varps.get(281) == 300 && Players.getLocal().getAnimation() == -1) {
                while (Inventory.getCount(new NameFilter<>("Tin ore")) != 2) {
                    Movement.walkTo(new Tile(3078, 9504));
                    Time.sleep(2000);
                    final GameObject rocks = GameObjects.getNearest("Rocks");
                    if (rocks != null) {
                        rocks.processAction("Mine");
                        Time.sleep(2000);
                    }
                }
            } else if (Varps.get(281) == 310) {
                while (Inventory.getCount(new NameFilter<>("Copper ore")) != 2) {
                    Movement.walkTo(new Tile(3082, 9501));
                    Time.sleep(2000);
                    final GameObject rocks = GameObjects.getNearest("Rocks");
                    if (rocks != null) {
                        rocks.processAction("Mine");
                        Time.sleep(2000);
                    }
                }
            } else if (Varps.get(281) == 320) {
                while (Inventory.getCount(new NameFilter<>("Bronze bar")) != 2) {
                    final GameObject furnace = GameObjects.getNearest(o -> "Furnace".equals(o.getName()) && o.containsAction("Use"));
                    final WidgetItem tin = Inventory.getFirst("Tin ore");
                    if (furnace != null && tin != null) {
                        tin.use(furnace);
                        Time.sleep(2000);
                    }
                }
            } else if (Varps.get(281) == 340) {
                final GameObject anvil = GameObjects.getNearest("Anvil");
                if (anvil != null) {
                    anvil.processAction("Smith");
                }
            } else if (Varps.get(281) == 350) {
                Time.sleep(500);
                Client.processAction(1, -1, 20447234, 57, "Smith 1", "Bronze dagger", 50, 50);
            } else if (Varps.get(281) == 360) {
                final GameObject gate = GameObjects.getNearest("Gate");
                if (gate != null) {
                    gate.processAction("Open");
                }
            }
        }
    }
}
