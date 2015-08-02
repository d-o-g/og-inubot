/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.reflection;

import java.util.*;

public class RefClass {

    private final Class<?> clazz;
    private final Set<RefField> fields;
    private final Set<RefMethod> methods;

    private Object instance;

    public RefClass(Object instance) {
        this.instance = instance;
        this.clazz = instance.getClass();
        this.fields = new HashSet<>();
        this.methods = new HashSet<>();
        Arrays.asList(clazz.getDeclaredMethods()).forEach(m -> methods.add(new RefMethod(this, m)));
        Arrays.asList(clazz.getDeclaredFields()).forEach(f -> fields.add(new RefField(this, f)));
    }

    public String getName() {
        return clazz.getName();
    }

    public Object getInstance() {
        try {
            return instance == null ? (instance = clazz.newInstance()) : instance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RefField getField(String name) {
        for (RefField f : fields) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public RefField[] getFields() {
        return fields.toArray(new RefField[fields.size()]);
    }

    public RefMethod[] getMethods() {
        return methods.toArray(new RefMethod[methods.size()]);
    }
}
