/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.InterfaceComponent;
import com.inubot.api.oldschool.Tab;
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
     * Gets the {@link InterfaceComponent} of all the tabs.
     *
     * @return All the tabs in raw {@link InterfaceComponent} format.
     */
    public static InterfaceComponent[] asComponents() {
        Tab[] values = Tab.values();
        InterfaceComponent[] tabs = new InterfaceComponent[values.length];
        for (int i = 0; i < values.length; i++)
            tabs[i] = values[i].getComponent();
        return tabs;
    }

    /**
     * Opens the provided tab.
     *
     * @param tab The tab to open.
     * @return <b>true</b> if the tab was successfully opened or already opened, <b>false</b> otherwise
     */
    public static boolean open(Tab tab) {
        if (Tabs.getOpen() == tab)
            return true;
        InterfaceComponent interfaceComponent = tab.getComponent();
        if (interfaceComponent != null) {
            Client.processAction(1, -1, interfaceComponent.getId(), ActionOpcodes.COMPONENT_ACTION, tab.toString(), "", 50, 50);
            return true;
        }
        return false;
    }
}
