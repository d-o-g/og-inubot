package com.inubot.bundledscripts.complete.chopper;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public enum Tree implements ChopperConstants {

    REGULAR("Tree", LOGS_REGULAR), OAK("Oak", LOGS_OAK), WILLOW("Willow", LOGS_WILLOW),
    MAPLE("Maple tree", LOGS_MAPLE), YEW("Yew", LOGS_YEW), MAGIC("Magic", LOGS_MAGIC);

    private final String name;
    private final int logsId;

    private Tree(String name, int logsId) {
        this.name = name;
        this.logsId = logsId;
    }

    public String getName() {
        return name;
    }

    public int getLogsId() {
        return logsId;
    }
}
