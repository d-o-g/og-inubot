/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.util;

import java.util.concurrent.TimeUnit;

public class StopWatch {

    private long start;
    private final long period;
    private long end, pause = -1;

    public StopWatch(final long period) {
        this.period = period;
        start = System.currentTimeMillis();
        end = start + period;
    }

    public boolean isPaused() {
        return pause != -1;
    }

    public void pause() {
        if (pause == -1) {
            pause = System.currentTimeMillis();
        }
    }

    public void resume() {
        if (pause != -1) {
            start += System.currentTimeMillis() - pause;
            pause = -1;
        }
    }

    public static String format(final long time) {
        final long days = time / (3600 * 24);
        final long hours = (time / 3600) - (days * 24);
        final long minutes = (time / 60) - (days * (60 * 24)) - (hours * 60);
        final long seconds = (time) - (days * (3600 * 24)) - (hours * 3600) - (minutes * 60);

        return days + ":" + hours + ":" + minutes + ":" + seconds;
    }

    public long getElapsed() {
        return System.currentTimeMillis() - start;
    }

    public long getElapsed(final TimeUnit unit) {
        return unit.convert(getElapsed(), TimeUnit.MILLISECONDS);
    }

    public long getRemaining() {
        return isRunning() ? end - System.currentTimeMillis() : 0;
    }

    public long getRemaining(final TimeUnit unit) {
        return unit.convert(getRemaining(), TimeUnit.MILLISECONDS);
    }

    public boolean isRunning() {
        return System.currentTimeMillis() < end;
    }

    public void reset() {
        end = System.currentTimeMillis() + period;
    }

    public long setEndIn(final long ms) {
        end = System.currentTimeMillis() + ms;
        return end;
    }

    public String toElapsedString() {
        return format(getElapsed());
    }

    public String toElapsedString(final TimeUnit unit) {
        return format(unit.convert(getElapsed(), TimeUnit.MILLISECONDS));
    }

    public String toRemainingString() {
        return format(getRemaining());
    }

    public String toRemainingString(final TimeUnit unit) {
        return format(unit.convert(getRemaining(), TimeUnit.MILLISECONDS));
    }

    public int getHourlyRate(final long value) {
        return (int) (value * 3600000.0D / getElapsed());
    }

    public long getStart() {
        return start;
    }
}