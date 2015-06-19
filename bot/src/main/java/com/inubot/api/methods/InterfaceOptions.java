/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.VarpBit;

/**
 * @author Dogerina
 * @since 04-06-2015
 */
public class InterfaceOptions {

    /** All bits of varp 1055 **/
    private static final VarpBit STONES_ARRANGEMENT;
    private static final VarpBit CHATBOX_MODE;
    private static final VarpBit SIDE_PANEL_MODE;

    static {
        STONES_ARRANGEMENT = VarpBit.get(4607);
        CHATBOX_MODE       = VarpBit.get(4608);
        SIDE_PANEL_MODE    = VarpBit.get(4609);
    }

    public static TabLayout getTabLayout() {
        return TabLayout.values()[STONES_ARRANGEMENT.getValue()];
    }

    public static ChatboxMode getChatboxMode() {
        return ChatboxMode.values()[CHATBOX_MODE.getValue()];
    }

    public static SidePanelMode getSidePanelMode() {
        return SidePanelMode.values()[SIDE_PANEL_MODE.getValue()];
    }

    public static ViewMode getViewMode() {
        if (!Game.isLoggedIn())
            return ViewMode.UNDETERMINED;
        int screenState = Game.getClient().getScreenState();
        for (ViewMode viewMode : ViewMode.values()) {
            if (viewMode.getState() == screenState)
                return viewMode;
        }
        throw new IllegalStateException("Unknown state..?");
    }

    public static enum ViewMode {

        FIXED_MODE(4),
        RESIZABLE_MODE(0),
        UNDETERMINED(-1);

        private final int state;

        private ViewMode(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    public static enum TabLayout {
        BOX, LINE
    }

    public static enum ChatboxMode {
        OPAQUE, TRANSPARENT
    }

    public static enum SidePanelMode {
        TRANSPARENT, OPAQUE
    }
}
