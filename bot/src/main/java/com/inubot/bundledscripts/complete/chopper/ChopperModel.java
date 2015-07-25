package com.inubot.bundledscripts.complete.chopper;

import com.inubot.bundledscripts.proframework.ProModel;

public class ChopperModel extends ProModel {

    private int logsChopped;
    private ProgressionType progressionType;

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

    public ProgressionType getProgressionType() {
        return progressionType;
    }

    public void setProgressionType(ProgressionType progressionType) {
        this.progressionType = progressionType;
    }
}
