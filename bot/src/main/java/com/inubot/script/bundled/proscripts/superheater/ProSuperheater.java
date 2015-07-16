/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.superheater;

import com.inubot.script.bundled.proscripts.framework.ProScript;

import java.util.Map;

/**
 * @author Dogerina
 * @since 16-07-2015
 */
public class ProSuperheater extends ProScript implements SuperheaterConstants {

    @Override
    public String getTitle() {
        return "ProSuperheater v1.00";
    }

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    @Override
    public int loop() {
        return 0;
    }
}
