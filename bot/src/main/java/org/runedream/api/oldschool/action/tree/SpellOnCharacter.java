/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

public abstract class SpellOnCharacter extends SpellOnEntityAction {

    public SpellOnCharacter(int opcode, int entityId) {
        super(opcode, entityId, 0, 0);
    }

    public final int getSignificantArgs() {
        return ARG0;
    }

    @Override
    public boolean accept(int opcode, int arg0, int arg1, int arg2) {
        return this.opcode == opcode && this.arg0 == arg0;
    }
}
