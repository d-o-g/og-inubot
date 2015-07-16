/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.util;

import java.util.concurrent.TimeUnit;

public class StopWatch {

    private final long start;
    private final long period;
    private long end;

    public StopWatch(final long period) {
        this.period = period;
        start = System.currentTimeMillis();
        end = start + period;
    }

    public static String format(final long time) {
        final StringBuilder t = new StringBuilder();
        final long total_secs = time / 1000;
        final long total_mins = total_secs / 60;
        final long total_hrs = total_mins / 60;
        final int secs = (int) total_secs % 60;
        final int mins = (int) total_mins % 60;
        final int hrs = (int) total_hrs % 60;
        if (hrs < 10) {
            t.append("0");
        }
        t.append(hrs);
        t.append(":");
        if (mins < 10) {
            t.append("0");
        }
        t.append(mins);
        t.append(":");
        if (secs < 10) {
            t.append("0");
        }
        t.append(secs);
        return t.toString();
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