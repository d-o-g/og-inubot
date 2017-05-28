package com.inubot.incomplete.septron;

import com.inubot.api.methods.*;
import com.inubot.api.methods.exchange.ExchangePricing;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Random;
import com.inubot.api.util.StopWatch;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Septron
 * @since June 27, 2015
 */
public class Willowcutter extends Script implements Paintable {

    private int price = 0, xp = 0;

    private StopWatch runtime;

    @Override
    public void render(Graphics2D graphics) {
        graphics.setFont(new Font("Dialog", Font.BOLD, 12));
        graphics.setColor(Color.YELLOW);
        graphics.drawString("PRO Willow Destruction", 10, 40);
        graphics.drawString("Runtime: " + runtime.toElapsedString(), 10, 55);

        int gain = Skills.getExperience(Skill.WOODCUTTING) - xp;
        int chopped = (int) (gain / 67.5);
        graphics.drawString("Chopped " + chopped + " logs", 10, 70);
        graphics.drawString("XP Gained: " + gain, 10, 85);
        graphics.drawString("Made: " + (chopped * price) + "gp", 10, 100);
    }

    @Override
    public boolean setup() {
        if (!Game.isLoggedIn()) {
            return false;
        }
        xp = Skills.getExperience(Skill.WOODCUTTING);
        price = ExchangePricing.get(1519);
        runtime = new StopWatch(0);
        return true;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 1000;
        if (!Inventory.isFull() && Players.getLocal().getAnimation() == -1) {
            GameObject tree = GameObjects.getNearest(obj -> {
                if (obj != null) {
                    if (obj.getName() != null) {
                        if (obj.getName().equals("Willow")) {
                            return true;
                        }
                    }
                }
                return false;
            });
            if (tree != null) {
                tree.processAction("Chop down");
            }
        } else if (Inventory.isFull()) {
            Widget depo = Interfaces.getWidget(192, 3);
            if (depo != null && depo.isVisible()) {
                depo.processAction("Deposit inventory");
            } else {
                GameObject box = GameObjects.getNearest(obj -> {
                    if (obj != null) {
                        if (obj.getName() != null) {
                            if (obj.getName().equals("Bank deposit box")) {
                                return true;
                            }
                        }
                    }
                    return false;
                });
                box.processAction("Deposit");
            }
        }
        return Random.nextInt(1500, 2500);
    }
}
