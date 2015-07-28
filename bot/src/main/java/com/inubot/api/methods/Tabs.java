/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.action.ActionOpcodes;

public class Tabs {

    /**
     * @return The current open tab.
     */
    public static Tab getOpen() {
        for (Tab tab : Tab.values()) {
            if (tab.isOpen())
                return tab;
        }
        return null;
    }

    /**
     * Gets the {@link Widget} of all the tabs.
     * @return All the tabs in raw {@link Widget} format.
     */
    public static Widget[] asWidgets() {
        Tab[] values = Tab.values();
        Widget[] tabs = new Widget[values.length];
        for (int i = 0; i < values.length; i++)
            tabs[i] = values[i].getWidget();
        return tabs;
    }

    /**
     * Opens the provided tab.
     * @param tab The tab to open.
     */
    public static void open(Tab tab) {
        if (Tabs.getOpen() == tab)
            return;
        Widget widget = tab.getWidget();
        if (widget != null) {
            Client.processAction(1, -1, widget.getId(), ActionOpcodes.WIDGET_ACTION, tab.toString(), "", 50, 50);
        }
    }
}
