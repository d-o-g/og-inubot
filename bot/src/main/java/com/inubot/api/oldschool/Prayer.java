/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

/**
 * @author unsigned
 * @since 21-05-2015
 */

import com.inubot.api.methods.Skills;
import com.inubot.api.util.CacheLoader;
import com.inubot.client.natives.RSItemDefinition;

public enum Prayer {
    THICK_SKIN(1, 0x1),
    BURST_OF_STRENGTH(4, 0x2),
    CLARITY_OF_THOUGHT(7, 0x4),
    ROCK_SKIN(10, 0x8),
    SHARP_EYE(8, 0x40000),
    MYSTIC_WILL(9, 0x80000),
    SUPERHUMAN_STRENGTH(13, 0x10),
    IMPROVED_REFLEXES(16, 0x20),
    RAPID_RESTORE(19, 0x40),
    RAPID_HEAL(22, 0x80),
    PROTECT_ITEM(25, 0x100),
    HAWK_EYE(26, 0x100000),
    MYSTIC_LORE(27, 0x200000),
    STEEL_SKIN(28, 0x200),
    ULTIMATE_STRENGTH(31, 0x400),
    INCREDIBLE_REFLEXES(34, 0x800),
    PROTECT_FROM_MAGIC(37, 0x1000),
    PROTECT_FROM_MISSILES(40, 0x2000),
    PROTECT_FROM_MELEE(43, 0x4000),
    EAGLE_EYE(44, 0x400000),
    MYSTIC_MIGHT(45, 0x800000),
    RETRIBUTION(46, 0x8000),
    REDEMPTION(49, 0x10000),
    SMITE(52, 0x20000),
    CHIVALRY(60, 0x2000000),
    PIETY(70, 0x4000000);

    private final int level;
    private final int bits;

    private Prayer(final int level, final int bits) {
        this.level = level;
        this.bits = bits;
    }

    public int getLevel() {
        return this.level;
    }

    public int getBits() {
        return this.bits;
    }

    public int getWidgetIndex() {
        return super.ordinal();
    }

    public int getInterfaceIndex() {
        return 271;
    }

    @Override
    public String toString() {
        final String name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
    }

    public static enum Bone {
        /*                ID     MEM       EXP  */
        BONES(526, false, 4.5),
        BIG_BONES(532, false, 15.0),
        ZOGRE_BONES(4812, true, 22.5),
        BABYDRAGON_BONES(634, true, 30.0),
        DRAGON_BONES(536, true, 72.0),
        WYVERN_BONES(5812, true, 72.0),
        RAURG_BONES(4832, true, 96.0),
        DAGANNOTH_BONES(6729, true, 125.0);

        private final int id;
        private final boolean membersOnly;
        private final double experienceGain;

        private Bone(final int id, final boolean membersOnly, final double experienceGain) {
            this.id = id;
            this.membersOnly = membersOnly;
            this.experienceGain = experienceGain;
        }

        public static Bone get(final int id) {
            for (final Bone bone : Bone.values()) {
                if (bone.id == id) {
                    return bone;
                }
            }
            return null;
        }

        public static int getBonesToExperience(final int currentExp, final int destXp, final Bone bone, final BurialType burialType) {
            int bones = 0;
            int curr = currentExp;
            while (curr < destXp) {
                bones++;
                curr += (int) bone.getExperienceGain(burialType);
            }
            return bones;
        }

        public static int getBonesToLevel(final int currentExp, final int destLevel, final Bone bone, final BurialType burialType) {
            return getBonesToExperience(currentExp, Skills.getExperienceAt(destLevel), bone, burialType);
        }

        public static int getBonesToLevelUp(final Bone bone, final BurialType burialType) {
            return getBonesToExperience(Skills.getExperience(Skill.PRAYER), Skills.getExperienceAt(Skills.getLevel(Skill.PRAYER) + 1),
                    bone, burialType);
        }

        public int getId() {
            return id;
        }

        public boolean isMembersOnly() {
            return membersOnly;
        }

        public double getExperienceGain() {
            return getExperienceGain(BurialType.BURY);
        }

        public double getExperienceGain(final BurialType type) {
            switch (type) {
                case GUILDED_ALTAR_NONE_LIT:
                    return experienceGain * 2.5;
                case GUILDED_ALTAR_ONE_LIT:
                    return experienceGain * 3;
                case GUILDED_ALTAR_BOTH_LIT:
                    return experienceGain * 3.5;
                case ECTOFUNTUS:
                    return experienceGain * 4;
                default: {
                    return experienceGain;
                }
            }
        }

        public RSItemDefinition getDefinition() {
            return CacheLoader.findItemDefinition(id);
        }

        public String getName() {
            return getDefinition().getName();
        }

        public static enum BurialType {
            BURY,
            GUILDED_ALTAR_NONE_LIT,
            GUILDED_ALTAR_ONE_LIT,
            GUILDED_ALTAR_BOTH_LIT,
            ECTOFUNTUS,
        }
    }
}

