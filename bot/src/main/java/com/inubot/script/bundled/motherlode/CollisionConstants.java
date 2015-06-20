/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

public interface CollisionConstants {

    int BASE_LENGTH = 69;
    int BASE_BOUNDARY = BASE_LENGTH;

    int WALL_NORTHWEST = 0x1;
    int WALL_NORTH = 0x2;
    int WALL_NORTHEAST = 0x4;
    int WALL_EAST = 0x8;
    int WALL_SOUTHEAST = 0x10;
    int WALL_SOUTH = 0x20;
    int WALL_SOUTHWEST = 0x40;
    int WALL_WEST = 0x80;

    int BLOCKED = 0x1280100;

}
