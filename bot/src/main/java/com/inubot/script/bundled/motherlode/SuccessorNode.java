/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import static com.inubot.script.bundled.motherlode.CollisionConstants.*;

public enum SuccessorNode {

    NORTH_WEST(-1, 1, 135) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_x > 0 && f_y < BASE_BOUNDARY &&
                    (here & (WALL_NORTHWEST | WALL_NORTH | WALL_WEST)) == 0
                    && (flags[f_x - 1][f_y + 1] & BLOCKED) == 0
                    && (flags[f_x][f_y + 1] & (BLOCKED | WALL_WEST)) == 0
                    && (flags[f_x - 1][f_y] & (BLOCKED | WALL_NORTH)) == 0);

        }
    }, NORTH(0, 1, 90) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_y < BASE_BOUNDARY && (here & WALL_NORTH) == 0
                    && (flags[f_x][f_y + 1] & BLOCKED) == 0);
        }
    }, NORTH_EAST(1, 1, 45) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_x > 0 && f_x < BASE_BOUNDARY && f_y < BASE_BOUNDARY &&
                    (here & (WALL_NORTHEAST | WALL_NORTH | WALL_EAST)) == 0
                    && (flags[f_x + 1][f_y + 1] & BLOCKED) == 0
                    && (flags[f_x][f_y + 1] & (BLOCKED | WALL_EAST)) == 0
                    && (flags[f_x + 1][f_y] & (BLOCKED | WALL_NORTH)) == 0);
        }
    }, EAST(1, 0, 0) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_x < BASE_BOUNDARY && (here & WALL_EAST) == 0
                    && (flags[f_x + 1][f_y] & BLOCKED) == 0);
        }
    }, SOUTH_EAST(1, -1, 315) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_x < BASE_BOUNDARY && f_y > 0 &&
                    (here & (WALL_SOUTHEAST | WALL_SOUTH | WALL_EAST)) == 0
                    && (flags[f_x + 1][f_y - 1] & BLOCKED) == 0
                    && (flags[f_x][f_y - 1] & (BLOCKED | WALL_EAST)) == 0
                    && (flags[f_x + 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0);
        }
    }, SOUTH(0, -1, 270) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_y > 0 && (here & WALL_SOUTH) == 0 &&
                    (flags[f_x][f_y - 1] & BLOCKED) == 0);
        }
    }, SOUTH_WEST(-1, -1, 225) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_x > 0 && f_y > 0 &&
                    (here & (WALL_SOUTHWEST | WALL_SOUTH | WALL_WEST)) == 0
                    && (flags[f_x - 1][f_y - 1] & BLOCKED) == 0
                    && (flags[f_x][f_y - 1] & (BLOCKED | WALL_WEST)) == 0
                    && (flags[f_x - 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0);
        }
    }, WEST(-1, 0, 180) {
        @Override
        public boolean walkable(int[][] flags, int f_x, int f_y, int here) {
            return (f_x > 0 && (here & WALL_WEST) == 0
                    && (flags[f_x - 1][f_y] & BLOCKED) == 0);
        }
    };

    public abstract boolean walkable(int[][] flags, int f_x, int f_y, int here);

    public final int dx, dy, angle;

    SuccessorNode(int dx, int dy, int angle) {
        this.dx = dx;
        this.dy = dy;
        this.angle = angle;
    }

}