/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action;

/**
 * A filter which allows an {@link com.inubot.api.oldschool.action.tree.Action} to identify itself
 */
public interface ActionFilter {
    boolean accept(int opcode, int arg0, int arg1, int arg2);
}
