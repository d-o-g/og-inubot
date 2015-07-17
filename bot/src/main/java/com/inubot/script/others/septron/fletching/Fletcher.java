package com.inubot.script.others.septron.fletching;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.bundled.proscripts.framework.ProScript;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class Fletcher extends ProScript {

    private final static DecimalFormat formatter = new DecimalFormat("#,###");

    private long animated;

    private int price;

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

    public String log() {
        int level = Skills.getCurrentLevel(Skill.FLETCHING);
        if (level >= 25 && level < 40) {
            return "Oak logs";
        } else if (level >= 40 && level < 55) {
            return "Willow logs";
        } else if (level >= 55 && level < 70) {
            return "Maple logs";
        } else if (level >= 65 && level < 80) {
            return "Yew logs";
        } else if (level >= 80 && level < 99) {
            return "Magic logs";
        }
        return null;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 600;

        if (Players.getLocal().getAnimation() != -1)
            animated = System.currentTimeMillis();
        if (System.currentTimeMillis() - animated > 1200L)
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
                Client.processAction(0, -1, bow().contains("short") ? 19922950 : 19922954, 30, "Make 10", "", 50, 50);
            }
        }
        return 600;
    }

    @Override
    public boolean setup() {
        price = Exchange.getPrice(62);
        return true;
    }

    @Override
    public String getTitle() {
        return "ProFletcher";
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        int actions = (int) (getTrackedSkill(Skill.FLETCHING).getGainedExperience() / 58.25);
        data.put("Actions P/H", getStopWatch().getHourlyRate(actions));
        data.put("GP P/H", formatter.format(getStopWatch().getHourlyRate(actions) * price));
        data.put("Made", actions);
    }
}
