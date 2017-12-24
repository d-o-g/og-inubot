package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.InterfaceComponent;
import com.inubot.api.oldschool.Item;
import com.inubot.api.util.Time;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

@Manifest(name = "Gold Bracelets", developer = "Septron", desc = "")
public class GoldBracelet extends ProScript {
    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    private long last = 0L;

    @Override
    public int loop() {
        if (Interfaces.canContinue())
            Interfaces.processContinue();
        InterfaceComponent component = Interfaces.get(446, 47);
        if (component != null && component.isVisible() && !component.isHidden()) {
            component.processAction("Make-All");
        } else {
            if (Inventory.getCount("Gold bar") > 0) {
                if (Players.getLocal().getAnimation() == -1) {
                    if (System.currentTimeMillis() - last > 3000) {
                        GameObject object = GameObjects.getNearest("Furnace");
                        if (object != null) {
                            Item item = Inventory.getFirst("Gold bar");
                            if (item != null) {
                                Inventory.use(item, object);
                                Time.await(() -> {
                                    InterfaceComponent component1 = Interfaces.get(446, 47);
                                    return component1 != null && component1.isVisible() && !component1.isHidden();
                                }, 5000);
                            }
                        }
                    }
                } else {
                    last = System.currentTimeMillis();
                }
            } else {
                if (Bank.isOpen()) {
                    if (Inventory.contains("Gold bracelet")) {
                        Bank.depositAllExcept("Bracelet mould");
                    } else {
                        Bank.withdrawAll("Gold bar");
                    }
                } else {
                    Bank.open();
                    Time.await(Bank::isOpen, 5000);
                }
            }
        }
        return 1000;
    }
}
