/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.RuneDream;

import java.awt.*;

/**
 * @author unsigned
 * @since 01-05-2015
 */
public class Login {

    public static final Point LOGIN = new Point(370, 325);
    public static final Point EXISTING_USER = new Point(465, 290);
    public static final int STATE_MAIN_MENU = 0;
    public static final int STATE_CREDENTIALS = 2;

    public static void setUsername(String to) {
        RuneDream.getInstance().getClient().setUsername(to);
    }

    public static void setPassword(String to) {
        RuneDream.getInstance().getClient().setPassword(to);
    }

    public static int getState() {
        return RuneDream.getInstance().getClient().getLoginState();
    }
}
