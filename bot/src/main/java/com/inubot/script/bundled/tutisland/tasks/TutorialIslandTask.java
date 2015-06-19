package com.inubot.script.bundled.tutisland.tasks;

/**
 * Created by luckruns0ut on 29/04/15.
 */
public abstract class TutorialIslandTask implements Runnable {
    public abstract boolean verify();
    public abstract void run();
}
