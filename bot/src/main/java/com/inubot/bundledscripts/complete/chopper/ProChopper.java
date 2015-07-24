/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.oldschool.event.MessageEvent.Type;
import com.inubot.bundledscripts.proframework.ProScript;

import java.util.Map;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public class ProChopper extends ProScript {

    private final ChopperController controller;

    public ProChopper() {
        this.controller = new ChopperController(new ChopperView(), new ChopperModel());
    }

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    @Override
    public int loop() {
        return 900;
    }

    @Override
    public void messageReceived(MessageEvent e) {
        if (e.getType() != Type.PLAYER && e.getText().toLowerCase().contains("logs")) {
            controller.getModel().incrementLogsChopped();
        }
    }
}
