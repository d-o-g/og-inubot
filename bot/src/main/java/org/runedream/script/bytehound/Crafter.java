package org.runedream.script.bytehound;

import org.runedream.api.methods.*;
import org.runedream.api.oldschool.GameObject;
import org.runedream.api.oldschool.Spell;
import org.runedream.api.oldschool.Widget;
import org.runedream.api.oldschool.WidgetItem;
import org.runedream.api.oldschool.action.tree.WidgetAction;
import org.runedream.api.util.Paintable;
import org.runedream.api.util.Time;
import org.runedream.api.util.filter.Filter;
import org.runedream.script.Script;

import java.awt.*;

/**
 * Created by Family on 5/18/2015.
 */
public class Crafter extends Script implements Paintable {

    private final Filter<WidgetItem> EXCEPT_FILTER = wi -> wi.getName().equals("Ring mould") || wi.getName().equals("Cosmic rune");

    @Override
    public int loop() {
        if (Interfaces.canContinue()) Interfaces.clickContinue();
        switch (getState()) {
            case ENCHANTING:
                WidgetItem sapphireRing = Inventory.getFirst("Sapphire ring");
                if (sapphireRing != null) {
                    Magic.cast(Spell.Modern.LVL_1_ENCHANT, sapphireRing);
                }
                break;
            case BANKING:
                bank();
                break;
            case CRAFTING:
                craft();
                break;
        }
        return 600;
    }

    @Override
    public void render(Graphics2D g) {

    }

    public void craft() {
        final Widget widget = Interfaces.getWidgetByText("What would you like to make?");
        if (widget != null && widget.isVisible()) {

        } else {
            final GameObject furnace = GameObjects.getNearest("Furnace");
            final WidgetItem goldBar = Inventory.getFirst("Gold bar");
            if (furnace != null && goldBar != null) {
                goldBar.use(furnace);
                Time.sleep(300);
            }
        }
    }

    public void bank() {
        if (Bank.isOpen()) {
            if (Inventory.getCount("Ring of recoil") == 13) {
                Bank.depositAllExcept(EXCEPT_FILTER);
            } else {
                Bank.withdraw(1607, 13);
                Time.sleep(400);
                Bank.withdraw(2357, 13);
            }
        } else {
            final GameObject bankBooth = GameObjects.getNearest("Bank booth");
            if (bankBooth != null) {
                bankBooth.processAction("Bank");
            }
        }
    }

    public ScriptState getState() {
        if (Inventory.getCount("Ring of recoil") == 13 || (!Inventory.contains("Gold bar") && !Inventory.contains("Ring of recoil"))) {
            return ScriptState.BANKING;
        } else if (Inventory.contains("Sapphire ring") && !Inventory.contains("Gold bar")) {
            return ScriptState.ENCHANTING;
        } else if (Inventory.contains("Gold bar")) {
            return ScriptState.CRAFTING;
        }
        return ScriptState.IDLE;
    }

    private enum ScriptState {

        ENCHANTING,
        BANKING,
        CRAFTING,
        IDLE,

    }
}
