/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods.traversal;

import com.inubot.Inubot;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Game;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.Varps;
import com.inubot.api.methods.traversal.graph.Web;
import com.inubot.api.oldschool.Locatable;
import com.inubot.api.oldschool.Player;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Random;
import com.inubot.api.util.Time;

public class Movement {

    private static Web web;

    static {
        web = new Web();
    }

    /**
     * @return <b>true</b> if run is enabled, <b>false</b> otherwise
     */
    public static boolean isRunEnabled() {
        return Varps.getBoolean(173);
    }

    /**
     * Attempts to toggle run on
     * @param on   Whether to toggle run on or not
     */
    public static void toggleRun(boolean on) {
        if (isRunEnabled() == on)
            return;
        Client.processAction(1, -1, 10485782, 57, "Toggle Run", "", Random.nextInt(10, 250), Random.nextInt(10, 250));
    }

    /**
     * @return The current run energy
     */
    public static int getRunEnergy() {
        return Inubot.getInstance().getClient().getEnergy();
    }

    /**
     * @param tiles The path to traverse
     */
    public static void walk(Tile... tiles) {
        for (int i = 0; i <= tiles.length; i++) {
            if (i == 0) {
                walkTo(tiles[i]);
            } else {
                while (tiles[i - 1].distance() > 3)
                    Time.sleep(300, 500);
                walkTo(tiles[i]);
            }
        }
    }

    /**
     * Walks to the given tile, if the tile is not in the current region then it will walk
     * to the corner of the region closest to the tile. For long paths, this is not recommended to be used.
     * @param tile The tile to walk to
     */
    public static void walkTo(Tile tile) {
        int x = tile.getRegionX();
        int y = tile.getRegionY();
        //if (x < 0 || x > 104 || y < 0 || y > 104)
            //return;
        if (x < 0) x = 0;
        if (x > 104) x = 104;
        if (y < 0)  y = 0;
        if (y > 104) y = 104;

        Inubot.getInstance().getClient().setViewportWalking(true);
        Inubot.getInstance().getClient().setHoveredRegionTileX(x);
        Inubot.getInstance().getClient().setHoveredRegionTileY(y);
    }

    /**
     * @param target
     * @return <b>true</b> if a tile is walkable
     */
    public static boolean isReachable(Locatable target) {
        return canReach(target.getLocation().getRegionX(), target.getLocation().getRegionY(), false);
    }

    /**
     * @param target
     * @return <b>true</b> if an object can be reached
     */
    public static boolean isObjectReachable(Locatable target) {
        return canReach(target.getLocation().getRegionX(), target.getLocation().getRegionY(), true);
    }

    private static boolean canReach(int destX, int destY, boolean isObject) {
        Player me = Players.getLocal();
        return me != null && canReach(me.getRegionX(), me.getRegionY(), destX, destY, isObject);
    }

    private static boolean canReach(int startX, int startY, int destX, int destY, boolean isObject) {
        if (startX > 103 || startY > 103 || startX < 0 || startY < 0)
            return false;
        // Documentation part:
        // The blocks info
        // When you can walk freely it's 0, also used to create a noclip
        int[][] via = new int[104][104];
        int[][] cost = new int[104][104];
        int[] tileQueueX = new int[4000];
        int[] tileQueueY = new int[4000];

        for (int xx = 0; xx < 104; xx++) {
            for (int yy = 0; yy < 104; yy++) {
                via[xx][yy] = 0;
                cost[xx][yy] = 99999999;
            }
        }

        int curX = startX;
        int curY = startY;
        via[startX][startY] = 99;
        cost[startX][startY] = 0;
        int head = 0;
        int tail = 0;
        tileQueueX[head] = startX;
        tileQueueY[head] = startY;
        head++;
        int pathLength = tileQueueX.length;
        int blocks[][] = Inubot.getInstance().getClient().getCollisionMaps()[Game.getPlane()].getFlags();
        while (tail != head) {
            curX = tileQueueX[tail];
            curY = tileQueueY[tail];

            if (!isObject && curX == destX && curY == destY) {
                return true;
            } else if (isObject
                    && ((curX == destX && curY == destY + 1)
                    || (curX == destX && curY == destY - 1)
                    || (curX == destX + 1 && curY == destY)
                    || (curX == destX - 1 && curY == destY))) {
                return true;
            }
            tail = (tail + 1) % pathLength;

            // Big and ugly block of code
            int thisCost = cost[curX][curY] + 1;
            // Can go south (by determining, whether the north side of the
            // south tile is blocked :P)
            if (curY > 0 && via[curX][curY - 1] == 0
                    && (blocks[curX][curY - 1] & 0x1280102) == 0) {
                tileQueueX[head] = curX;
                tileQueueY[head] = curY - 1;
                head = (head + 1) % pathLength;
                via[curX][curY - 1] = 1;
                cost[curX][curY - 1] = thisCost;
            }
            // Can go west
            if (curX > 0 && via[curX - 1][curY] == 0
                    && (blocks[curX - 1][curY] & 0x1280108) == 0) {
                tileQueueX[head] = curX - 1;
                tileQueueY[head] = curY;
                head = (head + 1) % pathLength;
                via[curX - 1][curY] = 2;
                cost[curX - 1][curY] = thisCost;
            }
            // Can go north
            if (curY < 104 - 1 && via[curX][curY + 1] == 0
                    && (blocks[curX][curY + 1] & 0x1280120) == 0) {
                tileQueueX[head] = curX;
                tileQueueY[head] = curY + 1;
                head = (head + 1) % pathLength;
                via[curX][curY + 1] = 4;
                cost[curX][curY + 1] = thisCost;
            }
            // Can go east
            if (curX < 104 - 1 && via[curX + 1][curY] == 0
                    && (blocks[curX + 1][curY] & 0x1280180) == 0) {
                tileQueueX[head] = curX + 1;
                tileQueueY[head] = curY;
                head = (head + 1) % pathLength;
                via[curX + 1][curY] = 8;
                cost[curX + 1][curY] = thisCost;
            }
            // Can go southwest
            if (curX > 0 && curY > 0 && via[curX - 1][curY - 1] == 0
                    && (blocks[curX - 1][curY - 1] & 0x128010e) == 0
                    && (blocks[curX - 1][curY] & 0x1280108) == 0
                    && (blocks[curX][curY - 1] & 0x1280102) == 0) {
                tileQueueX[head] = curX - 1;
                tileQueueY[head] = curY - 1;
                head = (head + 1) % pathLength;
                via[curX - 1][curY - 1] = 3;
                cost[curX - 1][curY - 1] = thisCost;
            }
            // Can go northwest
            if (curX > 0 && curY < 104 - 1 && via[curX - 1][curY + 1] == 0
                    && (blocks[curX - 1][curY + 1] & 0x1280138) == 0
                    && (blocks[curX - 1][curY] & 0x1280108) == 0
                    && (blocks[curX][curY + 1] & 0x1280120) == 0) {
                tileQueueX[head] = curX - 1;
                tileQueueY[head] = curY + 1;
                head = (head + 1) % pathLength;
                via[curX - 1][curY + 1] = 6;
                cost[curX - 1][curY + 1] = thisCost;
            }
            // Can go southeast
            if (curX < 104 - 1 && curY > 0 && via[curX + 1][curY - 1] == 0
                    && (blocks[curX + 1][curY - 1] & 0x1280183) == 0
                    && (blocks[curX + 1][curY] & 0x1280180) == 0
                    && (blocks[curX][curY - 1] & 0x1280102) == 0) {
                tileQueueX[head] = curX + 1;
                tileQueueY[head] = curY - 1;
                head = (head + 1) % pathLength;
                via[curX + 1][curY - 1] = 9;
                cost[curX + 1][curY - 1] = thisCost;
            }
            // can go northeast
            if (curX < 104 - 1 && curY < 104 - 1
                    && via[curX + 1][curY + 1] == 0
                    && (blocks[curX + 1][curY + 1] & 0x12801e0) == 0
                    && (blocks[curX + 1][curY] & 0x1280180) == 0
                    && (blocks[curX][curY + 1] & 0x1280120) == 0) {
                tileQueueX[head] = curX + 1;
                tileQueueY[head] = curY + 1;
                head = (head + 1) % pathLength;
                via[curX + 1][curY + 1] = 12;
                cost[curX + 1][curY + 1] = thisCost;
            }
        }
        return false;
    }

    /**
     * @return The current {@link com.inubot.api.methods.traversal.graph.Web} graph object
     */
    public static Web getWeb() {
        return web;
    }

    public static void setWeb(Web web) {
        Movement.web = web;
    }
}
