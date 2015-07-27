package me.mad.modules;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Random;
import com.inubot.api.util.Time;

/**
 * Created by mad on 7/25/15.
 */
public interface Tutorial {

    static int setting() {
        return Varps.get(281);
    }

    static boolean interact(final String npcName, final String action, Tile... tiles) {
        Npc npc = Npcs.getNearest(npcName);
        if(npc !=null && !isAnimating() && !Players.getLocal().isMoving() && !npc.isHealthBarVisible()) {
            npc.processAction(action);

        } else {
            if (tiles.length < 0) return false;

            if (Movement.getRunEnergy() > Random.nextInt(40, 50) && !Movement.isRunEnabled()) {
                Movement.toggleRun(true);
            }

            if (npc == null) {
                Movement.walkTo(tiles[0]);
                while (Players.getLocal().isMoving()) {
                    Time.sleep(50);
                }
            }
        }
        return true;
    }

    static boolean interactGB(final String npcName, final String action, Tile... tiles) {
        final GameObject npc;

        if(tiles.length == 1) {
            npc = GameObjects.getNearest(gameObject -> gameObject.getName() != null &&
                    gameObject.getName().equals(npcName)
                    && gameObject.getLocation().equals(tiles[0]));

        } else npc = GameObjects.getNearest(npcName);

        if(npc !=null && !isAnimating() && !Players.getLocal().isMoving()) {
            npc.processAction(action);

        } else {
            if(tiles.length < 2) return false;

            if(Movement.getRunEnergy() > Random.nextInt(40,50) && !Movement.isRunEnabled()) {
                Movement.toggleRun(true);
            }

            Movement.walkTo(tiles[1].derive(1,2));
        }
        return true;
    }

    static boolean useItemOn(String item, String item2) {
        final WidgetItem invItem = Inventory.getFirst(item);
        final WidgetItem invItem2 = Inventory.getFirst(item2);
        if(!isAnimating() && !Players.getLocal().isMoving()) {
            Inventory.use(invItem, invItem2);
        }
        return true;
    }

    static boolean useItemOn(String item, GameObject item2) {
        final WidgetItem invItem = Inventory.getFirst(item);
        if(!isAnimating() && !Players.getLocal().isMoving()) {
            Inventory.use(invItem, item2);
        }
        return true;
    }

    static boolean openTab(Tab tab) {
        Tabs.open(tab);
        return Time.await(tab::isOpen, 1200);
    }


    static boolean isChatOpen() {
        return Interfaces.canContinue();
    }

    static boolean continueChat() {
        return Interfaces.processContinue();
    }

    static boolean isAnimating() {
        return Players.getLocal().getAnimation() != -1;
    }
}
