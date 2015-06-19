/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.RuneDream;
import org.runedream.api.oldschool.Locatable;
import org.runedream.api.oldschool.Tile;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Camera {

    public static int getX() {
        return RuneDream.getInstance().getClient().getCameraX();
    }

    public static int getY() {
        return RuneDream.getInstance().getClient().getCameraY();
    }

    public static int getZ() {
        return RuneDream.getInstance().getClient().getCameraZ();
    }

    public static int getAltitude() {
        return RuneDream.getInstance().getClient().getCameraPitch();
    }

    public static int getYaw() {
        return RuneDream.getInstance().getClient().getCameraYaw();
    }

    public static int getAngle() {
        return (int) ((360D / 2048) * Math.min(2047 - getYaw(), 2048));
    }

    public static int angleTo(Locatable l) {
        Tile l0 = Players.getLocal().getLocation();
        int angle = 90 - ((int) Math.toDegrees(Math.atan2(l.getY() - l0.getY(), l.getX() - l0.getX())));
        if (angle < 0)
            angle += 360;
        return angle % 360;
    }
}
