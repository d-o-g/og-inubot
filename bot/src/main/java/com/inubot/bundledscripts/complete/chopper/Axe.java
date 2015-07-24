package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public enum Axe implements ChopperConstants {

    BRONZE(BRONZE_AXE, 1), IRON(IRON_AXE, 1), STEEL(STEEL_AXE, 10),
    MITHRIL(MITHRIL_AXE, 21), ADAMANT(ADAMANT_AXE, 30),
    RUNE(RUNE_AXE, 40), DRAGON(DRAGON_AXE, 60);

    private static final int[] ALL_AXE_IDS = new int[]{BRONZE_AXE, IRON_AXE, STEEL_AXE, MITHRIL_AXE,
            ADAMANT_AXE, RUNE_AXE, DRAGON_AXE};

    private final int itemId;
    private final int minimumAttackLevel;

    private Axe(int itemId, int minimumAttackLevel) {
        this.itemId = itemId;
        this.minimumAttackLevel = minimumAttackLevel;
    }

    public static Axe getBest() {
        return forLevel(Skills.getLevel(Skill.WOODCUTTING));
    }

    public static Axe forLevel(int level) {
        if (level >= 61) {
            return DRAGON;
        } else if (level >= 41) {
            return RUNE;
        } else if (level >= 31) {
            return ADAMANT;
        } else if (level >= 21) {
            return MITHRIL;
        } else if (level >= 6) {
            return STEEL;
        }
        return IRON;
    }

    public static int[] getIds() {
        return ALL_AXE_IDS;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getRequiredAttackLevel() {
        return this.minimumAttackLevel;
    }
}
