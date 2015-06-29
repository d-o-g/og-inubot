/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.fisher;

import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.util.Paintable;
import com.inubot.script.Script;

import java.awt.*;

public class AutoFisherPRO extends Script implements Paintable {

    private int caught = 0;

    @Override
    public int loop() {
        return 0;
    }

    @Override
    public void render(Graphics2D g) {

    }

    @Override
    public void messageReceived(MessageEvent e) {
        if (e.getType() == MessageEvent.Type.SERVER && e.getText().contains("You catch")) {
            caught++;
        }
    }
}
