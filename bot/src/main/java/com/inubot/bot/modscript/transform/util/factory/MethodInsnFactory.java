/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util.factory;

import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class MethodInsnFactory extends AbstractInsnFactory<MethodInsnNode, MethodInsnFactory> {

    public MethodInsnFactory(final MethodInsnNode insn) {
        super(insn);
    }

    public MethodInsnFactory(final int opcode, final String owner, final String name, final String desc, final boolean iface) {
        this(new MethodInsnNode(opcode, owner, name, desc, iface));
    }

    public final MethodInsnFactory iface(final boolean itf) {
        return super.deploy(() -> {
            super.getInstruction().itf = itf;
            return this;
        });
    }

    public final MethodInsnFactory opcode(final int newOpcode) {
        return super.deploy(() -> {
            super.getInstruction().setOpcode(newOpcode);
            return this;
        });
    }

    public final MethodInsnFactory name(final String newName) {
        return super.deploy(() -> {
            super.getInstruction().name = newName;
            return this;
        });
    }

    public final MethodInsnFactory owner(final String newOwner) {
        return super.deploy(() -> {
            super.getInstruction().owner = newOwner;
            return this;
        });
    }

    public final MethodInsnFactory desc(final String newDesc) {
        return super.deploy(() -> {
            super.getInstruction().desc = newDesc;
            return this;
        });
    }
}
