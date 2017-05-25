/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.Locatable;
import com.inubot.api.oldschool.Player;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.Widget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Projection {

    public static final int JAGEX_CIRCULAR_ANGLE = 2048;
    public static final double ANGULAR_RATIO = 360D / JAGEX_CIRCULAR_ANGLE;
    public static final double JAGEX_RADIAN = Math.toRadians(ANGULAR_RATIO);

    public static final int[] SIN_TABLE = new int[JAGEX_CIRCULAR_ANGLE];
    public static final int[] COS_TABLE = new int[JAGEX_CIRCULAR_ANGLE];

    static {
        for (int i = 0; i < JAGEX_CIRCULAR_ANGLE; i++) {
            SIN_TABLE[i] = (int) (65536.0D * Math.sin((double) i * JAGEX_RADIAN));
            COS_TABLE[i] = (int) (65536.0D * Math.cos((double) i * JAGEX_RADIAN));
        }
    }

    public static Point groundToViewport(int strictX, int strictY, int height) {
        if (strictX >= 128 && strictX <= 13056 && strictY >= 128 && strictY <= 13056) {
            int alt = Camera.getPitch();
            if (alt < 0) {
                return null;
            }
            int yaw = Camera.getYaw();
            if (yaw < 0) {
                return null;
            }
            int elevation = getGroundHeight(strictX, strictY) - height;
            strictX -= Camera.getX();
            strictY -= Camera.getY();
            elevation -= Camera.getZ();
            int altSin = SIN_TABLE[alt];
            int altCos = COS_TABLE[alt];
            int yawSin = SIN_TABLE[yaw];
            int yawCos = COS_TABLE[yaw];
            int angle = strictY * yawSin + strictX * yawCos >> 16;
            strictY = strictY * yawCos - strictX * yawSin >> 16;
            strictX = angle;
            angle = elevation * altCos - strictY * altSin >> 16;
            strictY = elevation * altSin + strictY * altCos >> 16;
            if (strictY == 0) {
                return null;
            }
            elevation = angle;
            //int xView = strictX * Projection.getZoom() / strictY + Projection.getWidth() / 2;
            //int yView = elevation * Projection.getZoom() / strictY + Projection.getHeight() / 2;
            //return new Point(xView, yView);
            return new Point(0,0);
            //if u need this just change it to use hardcoded zoom for now
        }
        return null;
    }

    public static int getGroundHeight(int x, int y) {
        int x1 = x >> 7;
        int y1 = y >> 7;
        if (x1 < 0 || x1 > 103 || y1 < 0 || y1 > 103)
            return 0;
        byte[][][] rules = getRenderRules();
        if (rules == null)
            return 0;
        int[][][] heights = getTileHeights();
        if (heights == null)
            return 0;
        int plane = Game.getPlane();
        if (plane < 3 && (rules[1][x1][y1] & 0x2) == 2)
            plane++;
        int x2 = x & 0x7F;
        int y2 = y & 0x7F;
        int h1 = heights[plane][x1][y1] * (128 - x2) + heights[plane][x1 + 1][y1] * x2 >> 7;
        int h2 = heights[plane][x1][y1 + 1] * (128 - x2) + heights[plane][x1 + 1][y1 + 1] * x2 >> 7;
        return h1 * (128 - y2) + h2 * y2 >> 7;
    }

    public static boolean isOnViewport(int x, int y) {
        return x > 1 && x < 516 && y > 1 && y < 337;
    }

    public static boolean isOnViewport(Point p) {
        return isOnViewport(p.x, p.y);
    }

    public static double distance(int x1, int y1, int x2, int y2) {
        int xd = x2 - x1;
        int yd = y2 - y1;
        return Math.sqrt(xd * xd + yd * yd);
    }

    public static double distance(Point p1, Point p2) {
        return distance(p1.x, p1.y, p2.x, p2.y);
    }

    public static double distance(Locatable t1, Locatable t2) {
        return distance(t1.getX(), t1.getY(), t2.getX(), t2.getY());
    }

    public static Point locatableToMap(Locatable locatable) {
        return groundToMap(locatable.getLocation().getX(), locatable.getLocation().getY());
    }

    public static Point groundToViewport(int strictX, int strictY) {
        try {
            if (strictX >= 128 && strictX <= 13056 && strictY >= 128 && strictY <= 13056) {
                int elevation = getGroundHeight(strictX, strictY);
                strictX -= Camera.getX();
                strictY -= Camera.getY();
                elevation -= Camera.getZ();
                int altSin = SIN_TABLE[Camera.getPitch()];
                int altCos = COS_TABLE[Camera.getPitch()];
                int yawSin = SIN_TABLE[Camera.getYaw()];
                int yawCos = COS_TABLE[Camera.getYaw()];
                int angle = strictY * yawSin + strictX * yawCos >> 16;
                strictY = strictY * yawCos - strictX * yawSin >> 16;
                strictX = angle;
                angle = elevation * altCos - strictY * altSin >> 16;
                strictY = elevation * altSin + strictY * altCos >> 16;
                if (strictY == 0)
                    return new Point(-1, -1);
                elevation = angle;
              //  int xView = strictX * Projection.getZoom() / strictY + Projection.getWidth() / 2;
             //   int yView = elevation * Projection.getZoom() / strictY + Projection.getHeight() / 2;
              //  return new Point(xView, yView);
                return new Point(0,0);
                //if u need this just change it to use hardcoded zoom for now
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return null;
    }

  //  public static int getZoom() {
    //    return Inubot.getInstance().getClient().getScreenZoom();
 //   }

    public static int getWidth() {
        return Inubot.getInstance().getClient().getScreenWidth();
    }

    public static int getHeight() {
        return Inubot.getInstance().getClient().getScreenHeight();
    }

    public static Point groundToMap(int x, int y, boolean ignoreDist) {
        Widget minimap = Minimap.getWidget();
        if (minimap == null)
            return null;
        Player local = Players.getLocal();
        if (local.getRaw() == null)
            return null;
        if (ignoreDist || distance(local, new Tile(x, y)) < 17) {
            x -= Game.getRegionBaseX();
            y -= Game.getRegionBaseY();
            int calcX = (x << 2) - (local.getStrictX() >> 5) + 2;
            int calcY = (y << 2) - (local.getStrictY() >> 5) + 2;
            int degree = Minimap.getScale() + Minimap.getRotation() & 0x7FF;
            int offset = Minimap.getOffset();
            int sin = SIN_TABLE[degree] * 256 / (offset + 256);
            int cos = COS_TABLE[degree] * 256 / (offset + 256);
            int centerX = (calcY * sin) + (calcX * cos) >> 16;
            int centerY = (calcX * sin) - (calcY * cos) >> 16;
            int screenX = 12 + (minimap.getX() + (minimap.getWidth() / 2)) + centerX;
            int screenY = 1 + (minimap.getY() + (minimap.getHeight() / 2)) + centerY;
            return new Point(screenX, screenY);
        }
        return null;
    }

    public static Point groundToMap(int x, int y) {
        return groundToMap(x, y, false);
    }

    public static Point locatableToViewport(Locatable locatable) {
        return groundToViewport(locatable.getLocation().getStrictX(), locatable.getLocation().getStrictY());
    }

    public static byte[][][] getRenderRules() {
        return Inubot.getInstance().getClient().getRenderRules();
    }

    public static int[][][] getTileHeights() {
        return Inubot.getInstance().getClient().getTileHeights();
    }

    public static Point[] getPointsIn(Shape shape) {
        List<Point> points = new ArrayList<>();
        Rectangle bounds = shape.getBounds();
        for (int x = bounds.x; x < bounds.getMaxX(); x++) {
            for (int y = bounds.y; y < bounds.getMaxY(); y++) {
                if (shape.contains(x, y))
                    points.add(new Point(x, y));
            }
        }
        return points.toArray(new Point[points.size()]);
    }
}
