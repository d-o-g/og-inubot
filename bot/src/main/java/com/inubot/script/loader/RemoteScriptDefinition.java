/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.loader;

import java.lang.annotation.Annotation;

public class RemoteScriptDefinition extends ScriptDefinition {

    private final String name, developer, desc;
    private final double version;

    public RemoteScriptDefinition(String name, String developer, String desc, double version) {
        this.name = name;
        this.developer = developer;
        this.desc = desc;
        this.version = version;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String developer() {
        return developer;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public double version() {
        return version;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
