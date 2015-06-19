/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.bot.modscript.transform.util.factory;

import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class FieldInsnFactory extends AbstractInsnFactory<FieldInsnNode, FieldInsnFactory> {

    public FieldInsnFactory(FieldInsnNode insn) {
        super(insn);
    }

    public FieldInsnFactory(final int opcode, final String owner, final String name, final String desc) {
        this(new FieldInsnNode(opcode, owner, name, desc));
    }

    public final FieldInsnFactory opcode(final int newOpcode) {
        return super.deploy(() -> {
            super.getInstruction().setOpcode(newOpcode);
            return this;
        });
    }

    public final FieldInsnFactory name(final String newName) {
        return super.deploy(() -> {
            super.getInstruction().name = newName;
            return this;
        });
    }

    public final FieldInsnFactory owner(final String newOwner) {
        return super.deploy(() -> {
            super.getInstruction().owner = newOwner;
            return this;
        });
    }

    public final FieldInsnFactory desc(final String newDesc) {
        return super.deploy(() -> {
            super.getInstruction().desc = newDesc;
            return this;
        });
    }
}

