/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package me.dogerina;

/**
 * @author Dogerina
 * @since 07-07-2015
 */
public enum Master {

    //TODO add assignments
    TURAEL  (1,    3),
    MAZCHNA (1,   20),
    VANNAKA (1,   40),
    CHAELDAR(1,   70),
    NIEVE   (1,   80),
    DURADEL (50, 100);

    private final int slayerLevel, combatLevel;
    private final Assignment[] assignments;

    private Master(int slayerLevel, int combatLevel, Assignment... assignments) {
        this.slayerLevel = slayerLevel;
        this.combatLevel = combatLevel;
        this.assignments = assignments;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public int getSlayerLevel() {
        return slayerLevel;
    }

    public Assignment[] getAssignments() {
        return assignments;
    }

    public String getName() {
        String name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace('_', ' ');
    }
}
