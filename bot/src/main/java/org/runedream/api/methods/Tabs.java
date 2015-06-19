/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.api.oldschool.Tab;
import org.runedream.api.oldschool.Widget;
import org.runedream.api.oldschool.action.ActionOpcodes;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Tabs {

    public static Tab getOpen() {
        for (Tab tab : Tab.values()) {
            if (tab.isOpen())
                return tab;
        }
        return null;
    }

    public static Widget[] asWidgets() {
        Tab[] values = Tab.values();
        Widget[] tabs = new Widget[values.length];
        for (int i = 0; i < values.length; i++)
            tabs[i] = values[i].getWidget();
        return tabs;
    }

    public static void open(Tab tab) {
        if (Tabs.getOpen() == tab)
            return;
        Widget widget = tab.getWidget();
        if (widget != null) {
            Client.processAction(1, -1, widget.getId(), ActionOpcodes.WIDGET_ACTION, tab.toString(), "", 50, 50);
        }
    }
}
