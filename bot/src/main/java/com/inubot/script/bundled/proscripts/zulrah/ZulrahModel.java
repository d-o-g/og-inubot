/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.zulrah;

import com.inubot.script.bundled.proscripts.framework.ProModel;

/**
 * @author Dogerina
 * @since 17-07-2015
 */
public class ZulrahModel extends ProModel {

    private int kills;

    ZulrahModel() {

    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
