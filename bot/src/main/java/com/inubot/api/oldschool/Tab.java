/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.InterfaceOptions;
import com.inubot.api.methods.InterfaceOptions.TabLayout;
import com.inubot.api.methods.InterfaceOptions.ViewMode;
import com.inubot.api.methods.Interfaces;

import java.lang.Character;

public enum Tab {

    CLAN_CHAT(28, 27, 30),
    FRIENDS_LIST(29, 28, 31),
    IGNORE_LIST(30, 29, 32),
    LOGOUT(31, 17, 33), //not an actual tab for line layout mode, but a button next to minimap
    OPTIONS(32, 30, 34),
    EMOTES(33, 31, 35),
    MUSIC_PLAYER(34, 32, 36),
    COMBAT(44, 41, 47),
    STATS(45, 42, 48),
    QUEST_LIST(46, 43, 49),
    INVENTORY(47, 44, 50),
    EQUIPMENT(48, 45, 51),
    PRAYER(49, 46, 52),
    MAGIC(50, 47, 53);

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

    public int getComponentIndex() {
        if (InterfaceOptions.getViewMode() == ViewMode.FIXED_MODE)
            return fixedModeIndex;
        return isBoxLayout() ? boxLayoutIndex : lineLayoutIndex;
    }

    public InterfaceComponent getComponent() {
        if (InterfaceOptions.getViewMode() == ViewMode.FIXED_MODE)
            return Interfaces.getComponent(MAIN_FIXED_MODE, getComponentIndex());
        return Interfaces.getComponent(isBoxLayout() ? BOX_LAYOUT_TABS : LINE_LAYOUT_TABS, getComponentIndex());
    }

    public boolean isOpen() {
        InterfaceComponent component = getComponent();
        return component != null && component.getMaterialId() != -1;
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
