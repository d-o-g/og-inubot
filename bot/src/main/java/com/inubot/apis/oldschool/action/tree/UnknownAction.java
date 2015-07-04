/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.oldschool.action.tree;

public class UnknownAction extends Action {

    public UnknownAction(int opcode, int arg0, int arg1, int arg2) {
        super(opcode, arg0, arg1, arg2);
    }

    @Override
    public int getSignificantArgs() {
        return SIG_ALL;
    }
}