/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.reflection;

import java.lang.reflect.Method;

public class RefMethod {

    private final RefClass parent;
    private final Method method;

    RefMethod(RefClass parent, Method method) {
        this.parent = parent;
        this.method = method;
    }

    public RefClass getParent() {
        return parent;
    }

    public Method getMethod() {
        return method;
    }
}
