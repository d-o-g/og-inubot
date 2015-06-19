/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.api.methods.ItemTables.Entry;
import org.runedream.api.oldschool.*;
import org.runedream.api.oldschool.action.ActionOpcodes;
import org.runedream.api.util.filter.Filter;
import org.runedream.api.util.filter.NameFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Inventory {

    public static final int INVENTORY_PARENT = 149;
    public static final int INVENTORY_ITEM_TABLE = 0;

    public static boolean isOpen() {
        return Tab.INVENTORY.isOpen();
    }

    public static WidgetItem[] getItems(Filter<WidgetItem> filter) {
        Widget inventory = Interfaces.getWidget(INVENTORY_PARENT, INVENTORY_ITEM_TABLE);
        if (inventory == null)
            return new WidgetItem[0];
        List<WidgetItem> items = new ArrayList<>();
        if (!Bank.isOpen()) {
            int[] itemIds = inventory.getItemIds();
            int[] itemQtys = inventory.getItemQuantities();
            if (itemIds != null) {
                for (int i = 0; i < itemIds.length; i++) {
                    int id = itemIds[i] - 1;
                    if (id == -1 || itemQtys[i] == 0)
                        continue;
                    WidgetItem item = new WidgetItem(inventory, i);
                    if (!filter.accept(item))
                        continue;
                    items.add(item);
                }
            }
        } else {
            Widget parent = Interfaces.getWidget(15, 3);
            if (parent != null) {
                int i = 0;
                for (Widget child : parent.getChildren()) {
                    i++;
                    if (child == null || child.getItemId() == -1)
                        continue;
                    WidgetItem item = new WidgetItem(child, i);
                    if (item != null) {
                        if (!filter.accept(item))
                            continue;
                        items.add(item);
                    }
                }
            }
        }
        return items.toArray(new WidgetItem[items.size()]);
    }

    public static WidgetItem[] getItems() {
        return getItems(Filter.always());
    }

    public static boolean contains(String s) {
        for (Entry widgetItem : ItemTables.getInventory()) {
            if (widgetItem.getName().equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static int getCount(boolean stacks) {
        int count = 0;
        for (int stack : ItemTables.getQuantitiesIn(ItemTables.INVENTORY))
             count += stacks ? stack : 1;
        return count;
    }

    public static int getCount() {
        return getCount(false);
    }

    public static int getCount(String name) {
        int i = 0;
        for (Entry entry : ItemTables.getInventory()) {
            if (entry.getName().equals(name)) {
                i++;
            }
        }
        return i;
    }

    public static int getCount(Filter<ItemTables.Entry> filter) {
        int count = 0;
        for (Entry entry : ItemTables.getInventory()) {
            if (filter.accept(entry))
                count++;
        }
        return count;
    }

    public static int getCountExcept(Filter<ItemTables.Entry> filter) {
        return getCount(Filter.not(filter));
    }

    public static boolean isFull() {
        return getCount() == 28;
    }

    public static boolean isEmpty() {
        return getCount() == 0;
    }

    public static WidgetItem getFirst(Filter<WidgetItem> filter) {
        for (WidgetItem item : getItems()) {
            if (item != null && filter.accept(item))
                return item;
        }
        return null;
    }

    public static WidgetItem getFirst(String name) {
        return getFirst(new NameFilter<>(false, name));
    }

    public static boolean dropAll(Filter<WidgetItem> filter) {
        for (WidgetItem item : Inventory.getItems(filter))
            item.processAction(ActionOpcodes.ITEM_ACTION_4, "Drop");
        return getItems(filter).length == 0;
    }

    public static boolean dropAllExcept(Filter<WidgetItem> filter) {
        for (WidgetItem item : Inventory.getItems(Filter.not(filter)))
            item.processAction(ActionOpcodes.ITEM_ACTION_4, "Drop");
        return getItems(filter).length == getCount();
    }

    public static void use(WidgetItem a, WidgetItem b) {
        a.use(b);
    }

    public static void use(WidgetItem a, GroundItem b) {
        a.use(b);
    }

    public static void use(WidgetItem a, GameObject b) {
        a.use(b);
    }

    public static void apply(Filter<WidgetItem> filter, Consumer<WidgetItem> application) {
        for (WidgetItem item : getItems(filter))
            application.accept(item);
    }

    public static void apply(Consumer<WidgetItem> application) {
        apply(Filter.always(), application);
    }
}
