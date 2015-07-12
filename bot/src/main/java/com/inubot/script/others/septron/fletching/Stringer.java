package com.inubot.script.others.septron.fletching;

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
import java.text.DecimalFormat;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class Stringer extends Script implements Paintable {

    private final StopWatch stopwatch = new StopWatch(0);

    private boolean animating() {
        for (int i = 0; i < 15; i++) {
            if (Players.getLocal().getAnimation() != -1)
                return true;
            Time.sleep(100);
        }
        return false;
    }

    private int price, cost, xp;

    public String bow() {
        int level = Skills.getCurrentLevel(Skill.FLETCHING);
        if (level >= 25 && level < 40) {
            return "Oak longbow (u)";
        } else if (level >= 40 && level < 55) {
            return "Willow longbow (u)";
        } else if (level >= 55 && level < 70) {
            return "Maple longbow (u)";
        } else if (level >= 70 && level < 80) {
            return "Yew longbow (u)";
        } else if (level >= 80 && level < 85) {
            return "Magic shortbow (u)";
        } else if (level >= 85 && level < 99) {
            return "Magic longbow (u)";
        }
        return null;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 600;
        if (animating())
            return 1000;
        if (Inventory.getCount(bow().replace(" (u)", "")) == 14 || Inventory.getCount() == 0) {
            if (!Bank.isOpen()) {
                Bank.open();
                return 600;
            }
            Bank.depositInventory();
            WidgetItem a = Bank.getFirst(new NameFilter<>(bow()));
            WidgetItem b = Bank.getFirst(new NameFilter<>("Bow string"));
            if (a != null && b != null) {
                a.processAction("Withdraw-14");
                b.processAction("Withdraw-14");
                return 700;
            }
        } else {
            if (Bank.isOpen()) {
                Bank.close();
                return 300;
            }
            WidgetItem a = Inventory.getFirst(bow());
            WidgetItem b = Inventory.getFirst("Bow string");
            if (a != null && b != null) {
                a.use(b);
                if (Time.await(() -> Interfaces.getWidget(t -> t.getId() == 20250627) != null, 1500)) {
                    Client.processAction(0, -1, 20250627, 30, "Make All", "", 50, 50);
                    return 1200;
                }
            }
        }
        return 1200;
    }

    @Override
    public boolean setup() {
        xp = Skills.getExperience(Skill.FLETCHING);
        cost = Exchange.getPrice(66) + Exchange.getPrice(1777);
        price = Exchange.getPrice(855);
        return true;
    }

    private final static DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    public void render(Graphics2D g) {

        int gain = Skills.getExperience(Skill.FLETCHING) - xp;
        int actions = (int) (gain / 58.25);

        int a = stopwatch.getHourlyRate(actions) * price;
        int b = stopwatch.getHourlyRate(actions) * cost;
        int c = a - b;

        g.setColor(Color.BLACK);
        g.drawRoundRect(5, 5, 150, 70, 5, 5);
        g.setColor(new Color(0, 255, 255, 60));
        g.fillRoundRect(5, 5, 150, 70, 5, 5);
        AWTUtil.drawBoldedString(g, "Time Running: " + stopwatch.toElapsedString(), 10, 20, Color.WHITE);
        AWTUtil.drawBoldedString(g, "Actions P/H: " + stopwatch.getHourlyRate(actions), 10, 35, Color.WHITE);
        AWTUtil.drawBoldedString(g, "GP P/H: " + formatter.format(c), 10, 50, Color.WHITE);
        AWTUtil.drawBoldedString(g, "Made: " + actions, 10, 65, Color.WHITE);
    }
}
