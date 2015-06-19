/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.others;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Script;
import com.inubot.api.oldschool.Skill;

import java.awt.*;

/**
 * @author unsigned
 * @since 28-05-2015
 */
public class Fletcher extends Script implements Paintable {

    private final StopWatch stopWatch = new StopWatch(0);
    private int exp;

    @Override
    public boolean setup() {
        super.getTickTasks().add(Interfaces::clickContinue);
        exp = Skills.getExperience(Skill.FLETCHING);
        return true;
    }

    @Override
    public int loop() {
        if (Inventory.getCount("Yew longbow") == 14 || Inventory.getCount() == 0) {
            if (!Bank.isOpen()) {
                Bank.open();
                return 600;
            }
            Bank.depositInventory();
            WidgetItem a = Bank.getFirst(new NameFilter<>("Yew longbow (u)"));
            WidgetItem b = Bank.getFirst(new NameFilter<>("Bow string"));
            if (a != null && b != null) {
                a.processAction("Withdraw-14");
                b.processAction("Withdraw-14");
                return 700;
            }
        } else if (!isInidle()) {
            if (Bank.isOpen()) {
                Bank.close();
                return 300;
            }
            WidgetItem a = Inventory.getFirst("Yew longbow (u)");
            WidgetItem b = Inventory.getFirst("Bow string");
            if (a != null && b != null) {
                a.use(b);
                if (Time.await(() -> Interfaces.getWidget(t -> t.getId() == 20250627) != null, 1500)) {
                    Client.processAction(0, -1, 20250627, 30, "Make All", "", 50, 50);
                    Time.sleep(800);
                }
            }
        }
        return 600;
    }

    private boolean isInidle() {
        for (int i = 0; i < 15; i++) {
            if (Players.getLocal().getAnimation() != -1)
                return true;
            Time.sleep(35);
        }
        return false;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "Runtime: " + stopWatch.toElapsedString(), 30, 30, Color.WHITE);
        int gain = (Skills.getExperience(Skill.FLETCHING) - exp);
        AWTUtil.drawBoldedString(g, "Exp: " + gain + " (" + stopWatch.getHourlyRate(gain) + ")", 30, 50, Color.WHITE);
    }
}
