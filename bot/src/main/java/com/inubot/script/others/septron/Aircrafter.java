package com.inubot.script.others.septron;

import com.inubot.api.methods.Bank;
import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class Aircrafter extends Script implements Paintable {

    private static Tile alter = new Tile(2986, 3295, 0);
    private static Tile bank = new Tile(3012, 3356, 0);

    @Override
    public int loop() {
        if (Players.getLocal().getLocation().getY() > 4000) {
            if (Inventory.isFull()) {
                GameObject alter = GameObjects.getNearest(go -> {
                    if (go == null)
                        return false;
                    if (go.getName() == null)
                        return false;
                    return go.getName().contains("Alter");
                });
                if (alter != null)
                    alter.processAction("Craft-rune");
            } else {
                GameObject alter = GameObjects.getNearest("Portal");
                if (alter != null)
                    alter.processAction("Use");
            }
        } else {
            if (Inventory.isFull()) {
                if (Players.getLocal().getLocation().distance(alter) > 10) {
                    Movement.walkTo(alter);
                } else {
                    GameObject alter = GameObjects.getNearest("Mysterious ruins");
                    WidgetItem item = Inventory.getFirst("Air talisman");
                    if (item != null && alter != null) {
                        item.processAction("Use");
                        alter.processAction("Use");
                    }
                }
            } else {
                if (Players.getLocal().getLocation().distance(bank) > 10) {
                    Movement.walkTo(bank);
                } else {
                    if (Bank.isOpen()) {
                        if (!Inventory.isFull())
                            Bank.depositAllExcept(item -> !item.getName().equals("Air talisman"));
                        else
                            Bank.withdraw(1436, 27);
                    } else {
                        Bank.open();
                    }
                }
            }
        }
        return 600;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(new Color(255, 0, 0, 60));
        g.drawRoundRect(0, 0, 100, 20, 5, 5);
        g.setColor(new Color(0, 0, 0, 20));
        g.fillRoundRect(0, 0, 99, 19, 5, 5);
    }
}
