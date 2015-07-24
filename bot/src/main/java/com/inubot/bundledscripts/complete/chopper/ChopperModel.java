/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.bundledscripts.proframework.ProModel;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public class ChopperModel extends ProModel {

    private boolean banking;
    private boolean progressive;
    private int logsChopped;

    ChopperModel() {

    }

    public int getLogsChopped() {
        return logsChopped;
    }

    public void setLogsChopped(int logsChopped) {
        this.logsChopped = logsChopped;
    }

    public void incrementLogsChopped() {
        logsChopped++;
    }

    public boolean isBanking() {
        return banking;
    }

    public void setBanking(boolean banking) {
        this.banking = banking;
    }

    public boolean isProgressive() {
        return progressive;
    }

    public void setProgressive(boolean progressive) {
        this.progressive = progressive;
    }
}
