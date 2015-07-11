/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogerina.slayer;

import com.inubot.api.oldschool.Tile;

/**
 * @author Dogerina
 * @since 07-07-2015
 */
public enum Assignment {

    CAVE_CRAWLER(new Tile(2789, 9996)),
    ROCKSLUG(new Tile(2798, 10016), new ItemConstraint("Bag of salt")),
    COCKATRICE(new Tile(2792, 10033), new ItemConstraint("Mirror shield", true)),
    PYREFIEND(new Tile(2760, 10004)),
    BASILISK(new Tile(2742, 10008), new ItemConstraint("Mirror shield", true)),
    JELLY(new Tile(2704, 10026)),
    TUROTH(new Tile(2724, 10002)),
    KURASK(new Tile(2699, 9997), new ItemConstraint("Leaf-bladed spear", true)),
    ANKOU(new Tile(2373, 9750)),
    KALPHITE_WORKER(new Tile(3765, 5850)),
    BLOODVELD(new Tile(2432, 9762, 1)),
    WALL_BEAST(new Tile(3219, 9587), new ItemConstraint("Spiny helmet", true));

    private final Tile location;
    private final Constraint[] constraints;

    private Assignment(Tile location, Constraint... constraints) {
        this.location = location;
        this.constraints = constraints;
    }

    public Tile getLocation() {
        return location;
    }

    public Constraint[] getConstraints() {
        return constraints;
    }

    public String getName() {
        String name = super.name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace('_', ' ');
    }
}
