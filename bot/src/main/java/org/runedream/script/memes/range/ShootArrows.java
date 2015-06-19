/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.range;

import org.runedream.api.methods.*;
import org.runedream.api.methods.traversal.Movement;
import org.runedream.api.oldschool.GameObject;
import org.runedream.api.oldschool.Tile;
import org.runedream.api.util.Time;
import org.runedream.script.memes.Action;

import java.util.Arrays;

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
                Mouse.hop(578, 138);
                Mouse.click(true);
            }
            Movement.walkTo(BASE);
            Time.sleep(1500, 2000);
        } else {
            GameObject obj = GameObjects.getNearest(g -> g.getName() != null && g.getName().equals("Target")
                    && Arrays.asList(g.getDefinition().getActions()).contains("Fire-at"));
            if (obj != null) {
                obj.processAction("Fire-at");
            }
        }
    }
}
