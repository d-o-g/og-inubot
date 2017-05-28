/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.api.methods.Interfaces;

public interface Spell {

    int MODERN = 0x1;
    int ANCIENT = 0x2;
    int LUNAR = 0x4;
    int COMMON = MODERN | LUNAR | ANCIENT;

    int getInterfaceIndex();

    int getWidgetIndex();

    int getLevel();

    int getBookFlags();

    default boolean isCommon() {
        return (getBookFlags() & COMMON) != 0;
    }

    default boolean isUncommon() {
        return !isCommon();
    }

    default Widget getWidget() {
        return Interfaces.getWidget(getInterfaceIndex(), getWidgetIndex());
    }

    public static enum Modern implements Spell {
        LUMBRIDGE_HOME_TELEPORT(0) {
            @Override
            public int getBookFlags() {
                return COMMON;
            }
        },
        WIND_STRIKE(1),
        CONFUSE(3),
        ENCHANT_CROSSBOW_BOLT(4),
        WATER_STRIKE(5),
        LVL_1_ENCHANT(7),
        EARTH_STRIKE(9),
        WEAKEN(11),
        FIRE_STRIKE(13),
        BONES_TO_BANANAS(15),
        WIND_BOLT(17),
        CURSE(19),
        BIND(20),
        LOW_LEVEL_ALCHEMY(21),
        WATER_BOLT(23),
        VARROCK_TELEPORT(25),
        LVL_2_ENCHANT(27),
        EARTH_BOLT(29),
        LUMBRIDGE_TELEPORT(31),
        TELEKINETIC_GRAB(33),
        FIRE_BOLT(35),
        FALADOR_TELEPORT(37),
        CRUMBLE_UNDEAD(39),
        HOUSE_TELEPORT(40),
        WIND_BLAST(41),
        SUPERHEAT_ITEM(43),
        CAMELOT_TELEPORT(45),
        WATER_BLAST(47),
        LVL_3_ENCHANT(49),
        IBAN_BLAST(50),
        SNARE(50),
        MAGIC_DART(50),
        ARDOUGNE_TELEPORT(51),
        EARTH_BLAST(53),
        HIGH_LEVEL_ALCHEMY(55),
        CHARGE_WATER_ORB(56),
        LVL_4_ENCHANT(57),
        WATCHTOWER_TELEPORT(58),
        FIRE_BLAST(59),
        CHARGE_EARTH_ORB(60),
        BONES_TO_PEACHES(60),
        SARADOMIN_STRIKE(60),
        CLAWS_OF_GUTHIX(60),
        FLAMES_OF_ZAMORAK(60),
        TROLLHEIM_TELEPORT(61),
        WIND_WAVE(62),
        CHARGE_FIRE_ORB(63),
        TELEPORT_TO_APE_ATOLL(64),
        WATER_WAVE(65),
        CHARGE_AIR_ORB(66),
        VULNERABILITY(66),
        LVL_5_ENCHANT(68),
        EARTH_WAVE(70),
        ENFEEBLE(73),
        TELEOTHER_LUMBRIDGE(74),
        FIRE_WAVE(75),
        ENTANGLE(79),
        STUN(80),
        CHARGE(80),
        TELEOTHER_FALADOR(82),
        TELE_BLOCK(85),
        TELEPORT_TO_BOUNTY_HUNTER(85) {
            @Override
            public int getBookFlags() {
                return COMMON;
            }
        },
        LVL_6_ENCHANT(87),
        TELEOTHER_CAMELOT(90);

        private final int level;

        private Modern(final int level) {
            this.level = level;
        }

        @Override
        public final int getWidgetIndex() {
            return this.ordinal() + 1;
        }

        @Override
        public final int getLevel() {
            return this.level;
        }

        public int getBookFlags() {
            return MODERN;
        }

        @Override
        public final int getInterfaceIndex() {
            return 218;
        }

        @Override
        public final String toString() {
            final String name = super.name();
            return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
        }
    }
}
