/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.reflection;

import com.inubot.bot.modscript.ModScript;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Field;
import java.util.Map;

public class ReflectionExplorer {

    private final Map<String, ClassNode> data;
    private final ClassLoader classLoader;

    public ReflectionExplorer(Map<String, ClassNode> data) {
        this.data = data;
        this.classLoader = ModScript.getClassLoader();
    }

    public RefClass getclass(String owner, String field, Object instance) {
        try {
            Class<?> clazz = classLoader.loadClass(owner);
            Field field_ = clazz.getDeclaredField(field);
            if (!field_.isAccessible()) {
                field_.setAccessible(true);
            }
            return new RefClass(field_.get(instance));
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public RefClass getclass(String owner, String field) {
        return getclass(owner, field, null);
    }

    public RefField getfield(String owner, String field, Object instance) {
        RefClass clazz = getclass(owner, field, instance);
        return clazz == null ? null : clazz.getField(field);
    }

    public RefField getstatic(String owner, String field) {
        return getfield(owner, field, null);
    }

    public Object getfieldValue(String owner, String field, Object instance) {
        try {
            RefField getfield = getfield(owner, field, instance);
            return getfield != null ? getfield.getField().get(null) : null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public Object getstaticValue(String owner, String field) {
        return getfieldValue(owner, field, null);
    }

    //TODO make some ui w/ jtree and have jpanel on side showing values for fields
}
