/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool;

import org.runedream.api.methods.InterfaceOptions;
import org.runedream.api.methods.InterfaceOptions.TabLayout;
import org.runedream.api.methods.InterfaceOptions.ViewMode;
import org.runedream.api.methods.Interfaces;

import java.lang.Character;

public enum Tab {

    CLAN_CHAT(23, 22, 25),
    FRIENDS_LIST(24, 23, 26),
    IGNORE_LIST(25, 24, 27),
    LOGOUT(26, 17, 28), //not an actual tab for line layout mode, but a button next to minimap
    OPTIONS(27, 25, 29),
    EMOTES(28, 26, 30),
    MUSIC_PLAYER(29, 27, 31),
    COMBAT(39, 36, 42),
    STATS(40, 37, 43),
    QUEST_LIST(41, 38, 44),
    INVENTORY(42, 39, 45),
    EQUIPMENT(43, 40, 46),
    PRAYER(44, 41, 47),
    MAGIC(45, 42, 48);

    private static final int BOX_LAYOUT_TABS = 161;
    private static final int LINE_LAYOUT_TABS = 164;
    private static final int MAIN_FIXED_MODE = 548;

    private final int boxLayoutIndex, lineLayoutIndex, fixedModeIndex;

    private Tab(int boxLayoutIndex, int lineLayoutIndex, int fixedModeIndex) {
        this.boxLayoutIndex = boxLayoutIndex;
        this.lineLayoutIndex = lineLayoutIndex;
        this.fixedModeIndex = fixedModeIndex;
    }

    private static String toTitleCase(String givenString) {
        StringBuilder sb = new StringBuilder();
        for (String anArr : givenString.split(" ")) {
            sb.append(Character.toUpperCase(anArr.charAt(0)))
                    .append(anArr.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public int getLineLayoutIndex() {
        return lineLayoutIndex;
    }

    /**
     * @return This will only return correctly for resizable box layout.
     *         @see #getFixedModeIndex For non resizable
     */
    public int getBoxLayoutIndex() {
        return boxLayoutIndex;
    }

    private boolean isBoxLayout() {
        return InterfaceOptions.getTabLayout() == TabLayout.BOX;
    }

    public int getWidgetIndex() {
        if (InterfaceOptions.getViewMode() == ViewMode.FIXED_MODE)
            return fixedModeIndex;
        return isBoxLayout() ? boxLayoutIndex : lineLayoutIndex;
    }

    public Widget getWidget() {
        if (InterfaceOptions.getViewMode() == ViewMode.FIXED_MODE)
            return Interfaces.getWidget(MAIN_FIXED_MODE, getWidgetIndex());
        return Interfaces.getWidget(isBoxLayout() ? BOX_LAYOUT_TABS : LINE_LAYOUT_TABS, getWidgetIndex());
    }

    public boolean isOpen() {
        Widget component = getWidget();
        return component != null && component.getTextureId() != -1;
    }

    @Override
    public String toString() {
        String name = super.name();
        //return name.charAt(0) + name.toLowerCase().substring(1).replace("_", " ");
        return toTitleCase(name.toLowerCase().replace("_", " "));
    }

    public int getFixedModeIndex() {
        return fixedModeIndex;
    }
}
