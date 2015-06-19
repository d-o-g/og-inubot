/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.loader;

import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;

import java.lang.reflect.Modifier;

public class ScriptFilter implements Filter<Class<?>> {
    @Override
    public boolean accept(Class<?> aClass) {
        return !Modifier.isAbstract(aClass.getModifiers()) && Script.class.isAssignableFrom(aClass);
    }
}
