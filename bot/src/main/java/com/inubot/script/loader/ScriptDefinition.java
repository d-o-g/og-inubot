/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.loader;

import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.lang.annotation.Annotation;

public class ScriptDefinition implements Manifest {

    private final Manifest manifest;
    private Class<? extends Script> scriptClass;

    public ScriptDefinition(Manifest manifest) {
        this.manifest = manifest;
    }

    protected ScriptDefinition() {
        this.manifest = null;
    }

    @Override
    public String name() {
        if (manifest == null) {
            throw new UnsupportedOperationException();
        }
        return manifest.name();
    }

    @Override
    public String developer() {
        if (manifest == null) {
            throw new UnsupportedOperationException();
        }
        return manifest.developer();
    }

    @Override
    public String desc() {
        if (manifest == null) {
            throw new UnsupportedOperationException();
        }
        return manifest.desc();
    }

    @Override
    public double version() {
        if (manifest == null) {
            throw new UnsupportedOperationException();
        }
        return manifest.version();
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }

    public final Class<? extends Script> getScriptClass() {
        return scriptClass;
    }

    public final void setScriptClass(Class<? extends Script> scriptClass) {
        this.scriptClass = scriptClass;
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof ScriptDefinition) {
            ScriptDefinition s = (ScriptDefinition) o;
            return s.version() == version() && s.name().equals(name()) && s.desc().equals(desc()) && s.developer().equals(developer());
        }
        return false;
    }
}
