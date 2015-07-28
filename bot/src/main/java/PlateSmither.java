/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

/**
 * @author Dogerina
 * @since 27-07-2015
 */
@Manifest(name = "bigdik plate smither", developer = "", desc = "")
public class PlateSmither extends Script {

    // * Works with both, steel and iron

    private static final int PARENT = 312;
    private static final int CHILD_BODY = 15;
    private static final int CHILD_2H = 6;

    private boolean steel;

    @Override
    public boolean setup() {
        this.steel = Inventory.contains("Steel bar");
        return true;
    }

    @Override
    public int loop() {
        State state = getState();
        if (state == State.SMITHING) {
            if (!isInidle()) {
                if (Interfaces.getWidget(PARENT, CHILD_BODY) == null) { //smithing interface is not open
                    GameObject anvil = GameObjects.getNearest("Anvil");
                    if (anvil != null) {
                        anvil.processAction("Smith");
                        Time.await(() -> Interfaces.getWidget(PARENT, CHILD_BODY) != null, 3000);
                    }
                } else {
                    Widget child = Interfaces.getWidget(PARENT, CHILD_BODY);
                    if (child != null) {
                        child.processAction("Smith 10");
                        Time.await(() -> Players.getLocal().getAnimation() == -1, 1500);
                    }
                }
            }
        } else if (state == State.BANKING) {
            if (!Bank.isOpen()) {
                Bank.open();
            } else {
                Bank.depositAllExcept(new NameFilter<>("Hammer"));
                WidgetItem item = Bank.getFirst(new NameFilter<>(getBarName()));
                if (item != null) {
                    item.processAction("Withdraw-All");
                }
            }
        }
        return 900;
    }

    private boolean isInidle() {
        for (int i = 0; i < 15; i++) {
            if (Players.getLocal().getAnimation() != -1)
                return true;
            Time.sleep(25);
        }
        return false;
    }

    private String getBarName() {
        return steel ? "Steel bar" : "Iron bar";
    }

    private State getState() {
        return Inventory.getCount(getBarName()) < 5 ? State.BANKING : State.SMITHING;
    }

    private enum State {
        SMITHING,
        BANKING
    }
}
