/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script;

import com.inubot.api.util.Time;

public abstract class LoopTask implements Task {

    private volatile boolean running = true;
    private volatile boolean paused = false;
    private volatile long cycles = 0;

    public abstract int loop();

    public final boolean isRunning() {
        return running;
    }

    public final void stop() {
        this.running = false;
        onExit();
        onFinish();
    }

    public void onFinish() {

    }

    public final boolean isPaused() {
        return paused;
    }

    public final void setPaused(final boolean paused) {
        this.paused = paused;
    }

    @Override
    public final void execute() {
        while (running) {
            try {
                if (paused)
                    continue;
                final int sleep = loop();
                cycles++;
                if (sleep < 0) {
                    System.err.println("Exiting Script");
                    break;
                }
                handleEvents();
                Time.sleep(sleep);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        running = false;
    }

    public void onExit() {
    }

    public void onPause() {
    }

    public void onResume() {
    }

    public abstract void handleEvents();

    public final long getCycles() {
        return cycles;
    }
}

