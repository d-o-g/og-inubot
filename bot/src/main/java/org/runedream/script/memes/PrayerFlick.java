/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes;

import org.runedream.api.methods.Prayers;
import org.runedream.api.oldschool.Prayer;
import org.runedream.script.Script;
import org.runedream.script.Task;

/**
 * @author unsigned
 * @since 21-05-2015
 */
public class PrayerFlick extends Script {

    @Override
    public boolean setup() {
        //whenever game cycles then flick
        super.getTickTasks().add(() -> Prayers.toggle(!Prayers.isActive(Prayer.PROTECT_FROM_MELEE), Prayer.PROTECT_FROM_MELEE));
        super.setForceIdleTimeClick(false);
        return true;
    }

    @Override
    public int loop() {
        return 500;
    }
}
