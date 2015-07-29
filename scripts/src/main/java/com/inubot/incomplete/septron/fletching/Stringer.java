package com.inubot.incomplete.septron.fletching;

import com.inubot.api.methods.*;
import com.inubot.api.methods.exchange.ExchangePricing;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.bundledscripts.proframework.ProScript;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class Stringer extends ProScript {

    private final StopWatch stopwatch = new StopWatch(0);

    private long animated;

    private int price, cost;

    public String bow() {
        int level = Skills.getCurrentLevel(Skill.FLETCHING);
        if (level >= 25 && level < 40) {
            return "Oak longbow (u)";
        } else if (level >= 40 && level < 55) {
            return "Willow longbow (u)";
        } else if (level >= 55 && level < 70) {
            return "Maple longbow (u)";
        } else if (level >= 70 && level < 85) {
            return "Yew longbow (u)";
        } else if (level >= 85 && level < 99) {
            return "Magic longbow (u)";
        }
        return null;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 600;
        if (Players.getLocal().getAnimation() != -1)
            animated = System.currentTimeMillis();
        if (System.currentTimeMillis() - animated < 1200)
            return 600;
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
        cost = ExchangePricing.get(70) + ExchangePricing.get(1777);
        price = ExchangePricing.get(859);
        return true;
    }

    private final static DecimalFormat formatter = new DecimalFormat("#,###");

    @Override
    public String getTitle() {
        return "Pro Stringer";
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        int actions = getTrackedSkill(Skill.FLETCHING).getGainedExperience() / 92;
        data.put("Time Running", stopwatch.toElapsedString());
        data.put("Actions P/H", stopwatch.getHourlyRate(actions));
        data.put("GP P/H", formatter.format(stopwatch.getHourlyRate(actions) * (price - cost)));
        data.put("Made", actions);
    }
}
