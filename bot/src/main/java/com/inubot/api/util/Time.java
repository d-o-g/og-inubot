/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.util;

import com.inubot.api.methods.Game;

import java.util.function.BooleanSupplier;

public class Time {

    private static final Object CYCLE_LOCK = new Object();

    public static boolean sleep(int millis) {
        try {
            if (millis < 0) {
                throw new IllegalArgumentException("millis must be positive!");
            }
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException ignored) {
            return false;
        }
    }

    public static boolean sleep(int min, int max) {
        return sleep(Random.nextInt(min, max));
    }

    public static boolean await(BooleanSupplier condition, int timeout) {
        long start = System.nanoTime() / 1000000;
        while (System.nanoTime() / 1000000 - start < timeout) {
            if (condition.getAsBoolean()) {
                return true;
            }
            Time.sleep(10, 20);
        }
        return false;
    }

    public static Object getCycleLock() {
        return CYCLE_LOCK;
    }

    public static boolean waitCycle() {
        return waitCycle(1000000);
    }

    public static boolean waitCycles(int cycles) {
        return waitCycles(cycles, 1000000);
    }

    public static boolean waitCycle(long timeout) {
        return waitCycles(1, timeout);
    }

    public static boolean waitCycles(int cycles, long timeout) {
        if (cycles < 0) {
            throw new IllegalArgumentException("cycles must be positive (" + cycles + ")");
        }
        if (cycles == 0) {
            return true;
        }
        long endTimeout = System.currentTimeMillis() + timeout;
        int destCycle = Game.getEngineCycle() + cycles;
        int currentCycle;
        synchronized (CYCLE_LOCK) {
            while ((currentCycle = Game.getEngineCycle()) < destCycle) {
                if (timeout <= 0) {
                    break;
                }
                try {
                    CYCLE_LOCK.wait(timeout);
                } catch (InterruptedException ignored) {
                    break;
                }
                timeout = (System.currentTimeMillis() - endTimeout);
            }
        }
        return currentCycle >= destCycle;
    }
}
