package com.inubot.api.util;

import com.inubot.api.oldschool.InterfaceComponent;

public final class InterfaceComponentConfig {

    private InterfaceComponentConfig() {
        throw new IllegalAccessError();
    }

    /**
     * @param config The InterfaceComponent config
     * @return {@code true} if the component is a dialog option. This also includes continue options
     * @see InterfaceComponent#getConfig()
     */
    public static boolean isDialogOption(int config) {
        return (config & 0x1) != 0;
    }

    /**
     * @param config    The InterfaceComponent config
     * @param optionIdx The index of the menu option
     * @return {@code true} if a right click option is enabled or not.
     * If an option is not allowed, it will not appear in the context menu.
     * I can't think of anything this applies to in Oldschool, however the summoning orb
     * in the main game has a "dismiss" option which isn't shown when a familiar is not summoned.
     * Maybe the XP tracker in oldschool? The "Hide" option is hidden (or changed?) when the
     * component is hidden, and the "Show" option is hidden when the component is shown
     */
    public static boolean isOptionEnabled(int config, int optionIdx) {
        return ((config >> (optionIdx + 1)) & 0x1) != 0;
    }

    /**
     * @param config The InterfaceComponent config
     * @return The application targets (what can the component be used on?).
     * e.g. for spells, strike spells can be cast on npcs and other players.
     * enchant can be cast on interface components, charge orb can be cast on objects
     */
    public static int getApplicationTargets(int config) {
        return (config >> 11) & 0x3f;
    }

    /**
     * @param config The InterfaceComponent config
     * @return The script event invocation depth. A higher depth means parent interfaces
     * will have the script invoked on them too
     */
    public static int getScriptEventDepth(int config) {
        return (config >> 17) & 0x7;
    }

    //up to date as of 31/05/2017 -dog
    //bit 20 tells whether the component is draggable or nah
    //bit 21 tells whether to allow spells or nah
    //bit 28 is related to drag and drop but i am not sure what it is
    //bit 29 tells whether to remove on drop or nah
    //bit 30 tells whether to allow item actions or nah
    //bit 31 tells whether to allow item usability or nah
    //TODO bits 22-27
    public static boolean isBitEnabled(int config, int bit) {
        return ((config >> bit) & 0x1) != 0;
    }

    //TODO for use-on
    public enum ApplicationTarget {

        GROUND_ITEM, NPC, OBJECT,
        OTHER_PLAYER, SELF, INTERFACE_COMPONENT;

        public int getFlags() {
            int flags = 1 << ordinal();
            flags &= ~(0x7f << 7);
            return flags;
        }
    }
}
