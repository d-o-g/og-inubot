package com.inubot.script.bundled.money;

import com.inubot.api.methods.Bank;
import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Time;
import com.inubot.script.Script;

import java.awt.*;

/**
 * Created by bytehound on 6/28/2015.
 */
public class Potato extends Script implements Paintable {

    private static final Tile BANK = new Tile(3094, 3243);
    private static final Tile POTATO = new Tile(3145, 3293);

    @Override
    public int loop() {
        if (Inventory.isFull()) {
            if (BANK.distance() > 7) {
                Movement.walkTo(BANK);
            } else if (Bank.isOpen()) {
                Bank.depositAll();
            } else {
                Bank.open();
                Time.sleep(2000);
            }
        } else {
            final GameObject potato = GameObjects.getNearest("Potato");
            if (potato != null) {
                final GameObject gate = GameObjects.getNearest(t -> "Gate".equals(t.getName()) && t.containsAction("Open"));
                if (gate != null) {
                    gate.processAction("Open");
                    return 2000;
                }
                if (Players.getLocal().getAnimation() == -1) {
                    potato.processAction("Pick");
                    Time.sleep(2000);
                }
            } else {
                Movement.walkTo(POTATO);
            }
        }
        return 300;
    }

    @Override
    public void render(Graphics2D g) {

    }
}
