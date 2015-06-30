/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.clues;

import com.inubot.api.util.Paintable;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Dogerina
 * @since 30-06-2015
 */
public class ClueSolver extends Script implements Paintable {

    public static final int[] AVOID_CLUE_IDS = {}; //the Mort'ton one and possibly wildy ones

    @Override
    public int loop() {
        return 800;
    }

    @Override
    public void render(Graphics2D g) {

    }
}
