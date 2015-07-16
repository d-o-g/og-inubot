/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.enchanter;

import com.inubot.api.util.StopWatch;
import com.inubot.script.bundled.proscripts.framework.ProModel;

/**
 * @author Dogerina
 * @since 16-07-2015
 */
public class EnchanterModel extends ProModel {

    private int startExperience;

    EnchanterModel() {

    }

    public int getStartExperience() {
        return startExperience;
    }

    public void setStartExperience(int startExperience) {
        this.startExperience = startExperience;
    }
}
