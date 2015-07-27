/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.client.GameCanvas;
import com.inubot.client.natives.oldschool.RSClient;

public class Game {

    public static final int STATE_CREDENTIALS_SCREEN = 10;
    public static final int STATE_LOADING_REGION = 25;
    public static final int STATE_IN_GAME = 30;

    /**
     * @return The current connection state of the game
     */
    public static int getState() {
        return Inubot.getInstance().getClient().getGameState();
    }

    public static int getCurrentWorld() {
        return getClient().getCurrentWorld();
    }

    public static boolean isMembersWorld() {
        return getClient().isMembersWorld();
    }

    /**
     * @return The current value of the x position of the map base
     */
    public static int getRegionBaseX() {
        return Inubot.getInstance().getClient().getBaseX();
    }

    /**
     * @return The current value of the y position of the map base
     */
    public static int getRegionBaseY() {
        return Inubot.getInstance().getClient().getBaseY();
    }

    /**
     * @return The current floor level
     */
    public static int getPlane() {
        return Inubot.getInstance().getClient().getPlane();
    }

    /**
     * @return <b>true</b> if the player is logged in, <b>false</b> otherwise
     */
    public static boolean isLoggedIn() {
        return getState() == STATE_IN_GAME;
    }

    /**
     * @return The current engine cycle value
     */
    public static int getEngineCycle() {
        return Inubot.getInstance().getClient().getEngineCycle();
    }

    /**
     * @return The {@link com.inubot.client.natives.oldschool.RSClient} internal instance
     */
    public static RSClient getClient() {
        return Inubot.getInstance().getClient();
    }

    public static GameCanvas getCanvas() {
        return Inubot.getInstance().getCanvas();
    }
}
