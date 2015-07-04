/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.util;

public interface Identifiable {

    /**
     * @return The ID assigned to this {@link Identifiable}
     */
    public int getId();

    /**
     * Throws an {@link java.lang.UnsupportedOperationException} if this {@link Identifiable}
     * has no name
     * @return The name assigned to this {@link Identifiable}
     */
    public default String getName() {
        throw new UnsupportedOperationException();
    }
}
