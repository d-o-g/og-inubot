/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.util;

import java.util.function.BooleanSupplier;

public class Time {

    public static boolean sleep(int millis) {
        try {
            if (millis < 0)
                throw new IllegalArgumentException("millis must be positive!");
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
            if (condition.getAsBoolean())
                return true;
            Time.sleep(5, 10);
        }
        return false;
    }
}
