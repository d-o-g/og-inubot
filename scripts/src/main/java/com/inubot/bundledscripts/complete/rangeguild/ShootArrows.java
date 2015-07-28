/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.rangeguild;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;

/**
 * @author unsigned
 * @since 25-04-2015
 */
public class ShootArrows implements Action {

    private static final Tile BASE = new Tile(2670, 3418);

    @Override
    public boolean validate() {
        return RangeGuild.isGameStarted() && Interfaces.getWidgets(RangeGuild.DIALOGUE_FILTER).length == 0;
    }

    @Override
    public void execute() {
        if (Players.getLocal().getLocation().distance(BASE) > 2) {
            if (!Movement.isRunEnabled()) {
                Movement.toggleRun(true);
            }
            Movement.walkTo(BASE);
            Time.sleep(1500, 2000);
        } else {
            GameObject obj = GameObjects.getNearest(g -> "Target".equals(g.getName()) && g.containsAction("Fire-at"));
            if (obj != null) {
                obj.processAction("Fire-at");
                Time.sleep(200);
            }
        }
    }
}
