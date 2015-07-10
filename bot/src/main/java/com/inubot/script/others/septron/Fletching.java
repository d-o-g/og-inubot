package com.inubot.script.others.septron;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Script;

/**
 * @author Septron
 * @since July 04, 2015
 */
public class Fletching extends Script {

    private boolean animating() {
        return Players.getLocal().getAnimation() != -1;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn())
            return 600;
        if (animating())
            return 600;
        if (Inventory.getCount("Longbow (u)") == 27 || Inventory.getCount() < 1) {
            if (!Bank.isOpen()) {
                Bank.open();
                return 600;
            }
            Bank.depositAllExcept(item -> item.getName().equals("Knife"));
            Time.sleep(600);
            WidgetItem a = Bank.getFirst(new NameFilter<>("Logs"));
            if (a != null)
                a.processAction("Withdraw-All");
            return 1200;
        } else {
            if (Bank.isOpen())
                Bank.close();
            WidgetItem a = Inventory.getFirst("Knife");
            WidgetItem b = Inventory.getFirst("Logs");
            if (a != null && b != null) {
                a.use(b);
                Time.sleep(600);
                if (Time.await(() -> Interfaces.getWidget(t -> t.getText().contains("Long Bow")) != null, 1500)) {
                    Client.processAction(0, -1, 19988495, 30, "Make 10", "", 50, 50);
                    return 1200;
                }
            }
        }
        return 600;
    }

}
