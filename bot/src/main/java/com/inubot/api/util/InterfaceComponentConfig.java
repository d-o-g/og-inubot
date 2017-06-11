package com.inubot.api.util;

/**
 * Contains various functions related to InterfaceComponent configurations.
 *
 * Hooks needed (R143)
 * Class gw        : IntegerNode
 * Field gw.i      : IntegerNode.value (int)
 *
 * Field hf.ci     : InterfaceComponent.config (int)
 * Field client.mp : Client.interfaceConfigs (NodeTable)
 *
 * <code>
 *  public int getConfig() {
 *      RSNodeTable<RSIntegerNode> configs = Game.getClient().getInterfaceConfigs();
 *      RSIntegerNode node = configs.lookup(((long) peer.getId() << 32) + (long) peer.getIndex());
 *      if (node != null) {
 *          return node.getValue();
 *      }
 *      return peer.getConfig();
 *  }
 * </code>
 *
 * @author Dogerina
 *
 * These functions have been confirmed to be working as of 31-05-2017
 */
public final class InterfaceComponentConfig {

    private InterfaceComponentConfig() {
        throw new IllegalAccessError();
    }


    //note: the isDialogOption method in this class does not work for old style tutorial island components

    /**
     * Note: To differentiate between continue and a chat option, text color can be used
     * @param config The InterfaceComponent config
     * @return {@code true} if the component is a dialog option. This also includes the continue options
     */
    public static boolean isDialogOption(int config) {
        return (config & 0x1) != 0;
    }

    /**
     * If an option is not allowed, it will not appear in the context menu.
     * I can't think of anything this applies to in Oldschool, however the summoning orb
     * in the main game has a "dismiss" option which isn't shown when a familiar is not summoned.
     *
     * @param config      The InterfaceComponent config
     * @param actionIndex The index of the menu option
     * @return {@code true} if a right click option is enabled or not.
     */
    public static boolean isActionEnabled(int config, int actionIndex) {
        return ((config >> (actionIndex + 1)) & 0x1) != 0;
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
     * @return The script event invocation depth. A higher depth means higher interfaces
     * in the hierarchy will have scripts invoked on them too
     */
    public static int getScriptEventDepth(int config) {
        return (config >> 17) & 0x7;
    }

    //bit 20 tells whether the component is draggable or nah
    //bit 21 tells whether to allow spells or nah
    //bit 28 is related to drag and drop but i am not sure what it is
    //bit 29 tells whether to remove on drop or nah
    //bit 30 tells whether to allow item actions or nah
    //bit 31 tells whether to allow item usability or nah
    //TODO bits 22-27
    public static boolean isBitSet(int config, int bit) {
        return ((config >> bit) & 0x1) != 0;
    }

    //TODO soon tm
    public enum ApplicationTarget {

        GROUND_ITEM, NPC, OBJECT,
        OTHER_PLAYER, SELF, INTERFACE_COMPONENT;

        public int getFlags() {
            int flags = 0x1 << ordinal();
            flags &= ~(0x7f << 7);
            return flags;
        }
    }
}
