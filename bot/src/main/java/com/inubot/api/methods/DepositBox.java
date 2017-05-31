package com.inubot.api.methods;

import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.InterfaceComponent;
import com.inubot.api.oldschool.Item;
import com.inubot.api.util.filter.Filter;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.api.util.filter.NameFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Septron
 * @since May 05, 2017
 */
public class DepositBox {

    private static final int INTERFACE = 192;

    private DepositBox() {
        throw new IllegalAccessError();
    }

    /**
     * @return {@code true} if the deposit box is open, {@code false} otherwise
     */
    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.get(INTERFACE, 0);
        return component != null && component.isVisible();
    }

    public static boolean open() {
        if (isOpen()) {
            return true;
        }
        GameObject p = GameObjects.getNearest(t -> t.containsAction("Deposit") && "Bank deposit box".equals(t.getName()));
        return p != null && p.processAction("Deposit");
    }

    public static boolean depositInventory() {
        InterfaceComponent component = Interfaces.get(INTERFACE, 3);
        if (component != null) {
            component.processAction("Deposit inventory");
            return true;
        }
        return false;
    }

    public static boolean depositAll(Filter<Item> predicate) {
        Item[] items = getItems(predicate);
        boolean success = false;
        outer:
        for (int i = 0; i < items.length; i++) {
            for (int j = 0; j < i; j++) {
                if (items[i].getId() == items[j].getId()) {
                    continue outer;
                }
            }
            success |= items[i].processAction("Deposit-All");
        }
        return success;
    }

    public static boolean depositAll(String... names) {
        return depositAll(new NameFilter<>(true, names));
    }

    public static boolean depositAll(int... ids) {
        return depositAll(new IdFilter<>(ids));
    }

    public static boolean depositAllExcept(Filter<Item> filter) {
        return depositAll(Filter.not(filter));
    }

    public static boolean depositAllExcept(int... ids) {
        return depositAllExcept(new IdFilter<>(ids));
    }

    public static boolean depositAllExcept(String... names) {
        return depositAllExcept(new NameFilter<>(names));
    }

    public static Item[] getItems(Filter<Item> filter) {
        InterfaceComponent parent = Interfaces.getComponent(INTERFACE, 2);
        if (parent != null) {
            List<Item> items = new ArrayList<>();
            int i = 0;
            for (InterfaceComponent child : parent.getComponents()) {
                i++;
                if (child.getItemId() != 6512) {
                    Item item = new Item(child, i);
                    if (!filter.accept(item))
                        continue;
                    items.add(item);
                }
            }
            return items.toArray(new Item[items.size()]);
        }
        return new Item[0];
    }
}
