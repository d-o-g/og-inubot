/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods;

import com.inubot.apis.oldschool.action.ActionOpcodes;
import com.inubot.apis.util.Time;

public class LogoutTab {

    private static final int BASE_WIDGET_HASH = 4521996;

    //TODO pretty sure this broke on resizable update
    public static void switchWorlds(int world) {
        if (world > 300)
            world -= 300;
        Client.processAction(1, -1, 11927557, ActionOpcodes.WIDGET_ACTION, "World switcher", "", 50, 50);
        Client.processAction(1, -1, BASE_WIDGET_HASH + world, ActionOpcodes.WIDGET_ACTION, "Switch", "", 50, 50);
        Time.sleep(300);
        Client.processAction(0, 1, 14352384, ActionOpcodes.BUTTON_DIALOG, "Continue", "", 50, 50);
        //^ may not be needed if u chose not to ask again
    }
}
