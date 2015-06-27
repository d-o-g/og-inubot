package com.inubot.script.others.septron;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Exchange;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.filter.Filter;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Dank Memes
 * @since June 18, 2015
 */
public class Powermine extends Script implements Paintable {

    private static final int[] TIN  = new int[] {14883, 14864};
    private static final int[] IRON = new int[] {13444, 13445, 13446};


    private static final int[] SELECTED = IRON;

    private int price = 0, xp = 0;

    private StopWatch runtime;

    @Override
    public void render(Graphics2D graphics) {
        graphics.setFont(new Font("Dialog", Font.BOLD, 12));
        graphics.setColor(Color.YELLOW);
        graphics.drawString("PRO Rock Destruction", 10, 40);
        graphics.drawString("Runtime: " + runtime.toElapsedString(), 10, 55);

        int gain = Skills.getExperience(Skill.MINING) - xp;
        int mined = gain / 35;
        graphics.drawString("Mined " + mined + " ore", 10, 70);
        graphics.drawString("XP Gained: " + gain, 10, 85);
        graphics.drawString("Made: " + (mined * price) + "gp", 10, 100);
    }

    @Override
    public boolean setup() {
        if (!Game.isLoggedIn()) {
            return false;
        }
        xp = Skills.getExperience(Skill.MINING);
        price = Exchange.price(440);
        runtime = new StopWatch(0);
        return true;
    }

    @Override
    public int loop() {
        if(Game.isLoggedIn()) {
            if (Inventory.isFull()) {
                Inventory.dropAll(item -> !item.getName().contains("pickaxe"));
            }
            if (Players.getLocal().getAnimation() == -1) {
                GameObject rock = GameObjects.getNearest(go -> {
                    if (go.distance(Players.getLocal().getLocation()) <= 2) {
                        for (int id : SELECTED) {
                            if (go.getId() == id)
                                return true;
                        }
                    }
                    return false;
                });
                if (rock != null)
                    rock.processAction("Mine");
                return 1500;
            } else {

            }
        }
        return 600;
    }
}
