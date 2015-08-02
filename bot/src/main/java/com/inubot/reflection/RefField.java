/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.reflection;

import java.lang.reflect.Field;

public class RefField {

    private final RefClass parent;
    private final Field field;

    RefField(RefClass parent, Field field) {
        this.parent = parent;
        this.field = field;
    }

    public RefClass getParent() {
        return parent;
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public String getTypeName() {
        return field.getType().getName();
    }
}
