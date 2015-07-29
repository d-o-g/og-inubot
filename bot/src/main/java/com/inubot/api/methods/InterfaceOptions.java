/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.VarpBit;

public class InterfaceOptions {

    public static final VarpBit VARP_ORBS_DISABLED;
    public static final VarpBit VARP_ACCEPT_AID;
    public static final VarpBit VARP_REMAINING_XP;
    public static final VarpBit VARP_LLNTD; //login logout notification timeout disabled...
    public static final VarpBit VARP_MOUSE_CAMERA_DISABLED; //login logout notification timeout disabled...
    public static final int VARP32_BRIGHTNESS = 166;
    public static final int VARP32_MUSIC_VOLUME = 168;
    public static final int VARP32_SOUND_EFFECT_VOLUME = 169;
    public static final int VARP32_AREA_SOUND_VOLUME = 872;
    public static final int VARP32_CHAT_EFFECTS_DISABLED = 171;
    public static final int VARP32_SPLIT_PRIVATE_CHAT_ENABLED = 287;
    public static final int VARP32_PROFANITY_FILTER_DISABLED = 1074;
    public static final int VARP32_MOUSE_BUTTONS_1 = 170;
    public static final int VARP32_ATTACK_OPTION_PRIORITY = 1074;

    static {
        VARP_ORBS_DISABLED = VarpBit.get(4084);
        VARP_ACCEPT_AID = VarpBit.get(4180);
        VARP_REMAINING_XP = VarpBit.get(4181);
        VARP_LLNTD = VarpBit.get(1627);
        VARP_MOUSE_CAMERA_DISABLED = VarpBit.get(4134);
    }

    /**
     * All bits of varp 1055 *
     */
    private static final VarpBit STONES_ARRANGEMENT;
    private static final VarpBit CHATBOX_MODE;
    private static final VarpBit SIDE_PANEL_MODE;

    static {
        STONES_ARRANGEMENT = VarpBit.get(4607);
        CHATBOX_MODE = VarpBit.get(4608);
        SIDE_PANEL_MODE = VarpBit.get(4609);
    }

    /**
     * @return The current {@link com.inubot.api.methods.InterfaceOptions.TabLayout} type
     */
    public static TabLayout getTabLayout() {
        return TabLayout.values()[STONES_ARRANGEMENT.getValue()];
    }

    /**
     * @return The current {@link com.inubot.api.methods.InterfaceOptions.ChatboxMode} type
     */
    public static ChatboxMode getChatboxMode() {
        return ChatboxMode.values()[CHATBOX_MODE.getValue()];
    }

    /**
     * @return The current {@link com.inubot.api.methods.InterfaceOptions.SidePanelMode} type
     */
    public static SidePanelMode getSidePanelMode() {
        return SidePanelMode.values()[SIDE_PANEL_MODE.getValue()];
    }

    /**
     * @return The current {@link com.inubot.api.methods.InterfaceOptions.ViewMode}.
     * This will be fixed or resizable all the time, unless the player is not logged in,
     * in which case undetermined will be returned
     */
    public static ViewMode getViewMode() {
        if (!Game.isLoggedIn())
            return ViewMode.UNDETERMINED;
        int screenState = 0;
        try {
            screenState = Game.getClient().getScreenState();
            for (ViewMode viewMode : ViewMode.values()) {
                if (viewMode.getState() == screenState)
                    return viewMode;
            }
        } catch (AbstractMethodError e) {
            return ViewMode.FIXED_MODE;
        }
        return ViewMode.FIXED_MODE;
    }

    public static boolean isAcceptingAid() {
        return VARP_ACCEPT_AID.booleanValue();
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

    public static class Controls {

        public static int getMouseButtons() {
            boolean b1 = Varps.getBoolean(VARP32_MOUSE_BUTTONS_1);
            return b1 ? 1 : 2;
        }

        public static boolean isMouseCameraEnabled() {
            return !VARP_MOUSE_CAMERA_DISABLED.booleanValue();
        }

        public static AttackOptionPriority getAttackOptionPriority() {
            final int id = Varps.get(VARP32_ATTACK_OPTION_PRIORITY);
            return AttackOptionPriority.values()[id];
        }

        public static enum AttackOptionPriority {
            // In client-respected order, don't change...
            COMBAT_LEVEL, // Depends on combat levels.
            LEFT_CLICK, // Left-click where available.
            RIGHT_CLICK // Always right-click.
        }

    }

    public static class Chat {

        public static boolean isChatEffectsEnabled() {
            return !Varps.getBoolean(VARP32_CHAT_EFFECTS_DISABLED);
        }

        public static boolean isSplitPrivateChatEnabled() {
            return Varps.getBoolean(VARP32_SPLIT_PRIVATE_CHAT_ENABLED);
        }

        public static boolean isProfanityFilterEnabled() {
            return !Varps.getBoolean(VARP32_PROFANITY_FILTER_DISABLED);
        }


        public static boolean isLoginLogoutNotificationTimeoutEnabled() { //TODO holy name
            return !VARP_LLNTD.booleanValue();
        }

    }

    public static class Audio {

        public static int getMusicVolume() {
            return Varps.get(VARP32_MUSIC_VOLUME);
        }

        public static int getSoundEffectVolume() {
            return Varps.get(VARP32_SOUND_EFFECT_VOLUME);
        }

        public static int getAreaSoundVolume() {
            return Varps.get(VARP32_AREA_SOUND_VOLUME);
        }

    }

    public static class Display { //TODO roofs enabled

        public static boolean isOrbsEnabled() {
            return !VARP_ORBS_DISABLED.booleanValue();
        }

        public static int getBrightness() {
            return Varps.get(VARP32_BRIGHTNESS);
        }

        public static boolean isRemainingXpOn() {
            return VARP_REMAINING_XP.booleanValue();
        }
    }
}
