/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import sun.nio.ch.DirectBuffer;

import java.nio.MappedByteBuffer;

public class Util {

    public static void clearMap(MappedByteBuffer buffer) {
        sun.misc.Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
        cleaner.clean();
    }
}