package com.inubot.script.others.septron;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Player;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class Fletching extends Script implements Paintable {

    private final StopWatch stopwatch = new StopWatch(0);

    private boolean animating() {
        return Players.getLocal().getAnimation() != -1;
    }

    private int price, xp;

    public String bow() {
        int level = Skills.getCurrentLevel(Skill.FLETCHING);
        if (level >= 25 && level < 40) {
            return "Oak longbow (u)";
        } else if (level > 40 && level < 55) {
            return "Willow longbow (u)";
        } else if (level > 55 && level < 65) {
            return "Maple longbow (u)";
        } else if (level > 65 && level < 70) {
            return "Yew shortbow (u)";
        } else if (level > 70 && level < 80) {
            return "Yew longbow (u)";
        } else if (level > 80 && level < 85) {
            return "Magic shortbow (u)";
        } else if (level > 85 && level < 99) {
            return "Magic longbow (u)";
        }
        return null;
    }

    public String log() {
        int level = Skills.getCurrentLevel(Skill.FLETCHING);
        if (level >= 25 && level < 40) {
            return "Oak logs";
        } else if (level > 40 && level < 55) {
            return "Willow logs";
        } else if (level > 55 && level < 65) {
            return "Maple logs";
        } else if (level > 65 && level < 80) {
            return "Yew logs";
        } else if (level > 80 && level < 99) {
            return "Magic logs";
        }
        return null;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 600;
        if (animating())
            return 600;
        if (Inventory.getCount(bow()) == 27 || Inventory.getCount() < 1) {
            if (!Bank.isOpen()) {
                Bank.open();
                return 600;
            }
            Bank.depositAllExcept(item -> item.getName().equals("Knife"));
            WidgetItem a = Bank.getFirst(new NameFilter<>(log()));
            if (a != null)
                a.processAction("Withdraw-All");
            else {
                stop();
            }
            return 1200;
        } else {
            if (Bank.isOpen())
                Bank.close();
            WidgetItem a = Inventory.getFirst("Knife");
            System.out.println(log());
            WidgetItem b = Inventory.getFirst(log());
            if (a != null && b != null) {
                a.use(b);
                Time.sleep(600);
                if (Time.await(() -> Interfaces.getWidget(t -> t.getText().contains("Bow")) != null, 1500)) {
                    Client.processAction(0, -1, bow().contains("short") ? 19922950 : 19922954, 30, "Make 10", "", 50, 50);
                    return 1200;
                }
            }
        }
        return 600;
    }

    @Override
    public boolean setup() {
        xp = Skills.getExperience(Skill.FLETCHING);
        price = Exchange.getPrice(62);
        return true;
    }

    @Override
    public void render(Graphics2D g) {

        int gain = Skills.getExperience(Skill.FLETCHING) - xp;
        int actions = (int) (gain / 58.25);

        g.setColor(Color.BLACK);
        g.drawRoundRect(5, 5, 150, 70, 5, 5);
        g.setColor(new Color(0, 255, 255, 60));
        g.fillRoundRect(5, 5, 150, 70, 5, 5);
        AWTUtil.drawBoldedString(g, "Time Running: " + stopwatch.toElapsedString(), 10, 20, Color.WHITE);
        AWTUtil.drawBoldedString(g, "Actions P/H: " + stopwatch.getHourlyRate(actions), 10, 35, Color.WHITE);
        AWTUtil.drawBoldedString(g, "GP P/H: " + stopwatch.getHourlyRate(actions) * price, 10, 50, Color.WHITE);
        AWTUtil.drawBoldedString(g, "Made: " + actions, 10, 65, Color.WHITE);
    }
}
