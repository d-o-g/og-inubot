/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods;

import com.inubot.Inubot;
import com.inubot.apis.oldschool.Locatable;
import com.inubot.apis.oldschool.Tile;

public class Camera {

    /**
     * @return The raw x position of the camera
     */
    public static int getX() {
        return Inubot.getInstance().getClient().getCameraX();
    }

    /**
     * @return The raw y position of the camera
     */
    public static int getY() {
        return Inubot.getInstance().getClient().getCameraY();
    }

    /**
     * @return The raw z position of the camera
     */
    public static int getZ() {
        return Inubot.getInstance().getClient().getCameraZ();
    }

    /**
     * @return The current altitude, or pitch of the camera
     */
    public static int getAltitude() {
        return Inubot.getInstance().getClient().getCameraPitch();
    }

    /**
     * @return The current yaw of the camera
     */
    public static int getYaw() {
        return Inubot.getInstance().getClient().getCameraYaw();
    }

    /**
     * @return The current camera angle
     */
    public static int getAngle() {
        return (int) ((360D / 2048) * Math.min(2047 - getYaw(), 2048));
    }

    /**
     * @param l The locatable
     * @return The camera angle to the given locatable
     */
    public static int angleTo(Locatable l) {
        Tile l0 = Players.getLocal().getLocation();
        int angle = 90 - ((int) Math.toDegrees(Math.atan2(l.getY() - l0.getY(), l.getX() - l0.getX())));
        if (angle < 0)
            angle += 360;
        return angle % 360;
    }
}
