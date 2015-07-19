/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.loader;

import java.io.IOException;

public abstract class ScriptLoader<T> extends ScriptFilter {
    public abstract void parse(T t) throws IOException, ClassNotFoundException;
    public abstract ScriptDefinition[] getDefinitions();
}
