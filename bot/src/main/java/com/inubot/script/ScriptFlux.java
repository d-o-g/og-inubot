/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script;

import com.inubot.Inubot;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class ScriptFlux {

    private Script running = null;

    public Script getRunning() {
        return running;
    }

    public void execute(Script runningScript) {
        stop();
        this.running = runningScript;
        this.running.start();
        Inubot.getInstance().getJMenuBar().updateButtonStates();
    }

    public void switchState() {
        if (running != null)
            running.setPaused(!running.isPaused());
    }

    public boolean isRunning() {
        return running != null;
    }

    public boolean isPaused() {
        return running != null && running.isPaused();
    }

    public void stop() {
        if (running != null) {
            running.stop();
            running = null;
        }
    }
}
