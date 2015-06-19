/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.RuneDream;
import com.inubot.client.natives.RSClient;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Game {

    public static final int STATE_CREDENTIALS_SCREEN = 10;
    public static final int STATE_LOADING_REGION = 25;
    public static final int STATE_IN_GAME = 30;

    public static int getState() {
        return RuneDream.getInstance().getClient().getGameState();
    }

    public static int getRegionBaseX() {
        return RuneDream.getInstance().getClient().getBaseX();
    }

    public static int getRegionBaseY() {
        return RuneDream.getInstance().getClient().getBaseY();
    }

    public static int getPlane() {
        return RuneDream.getInstance().getClient().getPlane();
    }

    public static boolean isLoggedIn() {
        return getState() == STATE_IN_GAME;
    }

    public static int getEngineCycle() {
        return RuneDream.getInstance().getClient().getEngineCycle();
    }

    public static RSClient getClient() {
        return RuneDream.getInstance().getClient();
    }
}
