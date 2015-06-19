/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.client.natives.ClientNative;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public abstract class Wrapper<T extends ClientNative> {

    protected final T raw;

    public Wrapper(T raw) {
        if (raw == null)
            throw new IllegalArgumentException("raw == null");
        this.raw = raw;
    }

    public T getRaw() {
        return raw;
    }

    //TODO an actual way to validate?
    public boolean validate() {
        return raw != null;
    }

    @Override
    public int hashCode() {
        return validate() ? raw.hashCode() : super.hashCode();
    }
}
