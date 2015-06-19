/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util.factory;

import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class VarInsnFactory extends AbstractInsnFactory<VarInsnNode, VarInsnFactory> {

    public VarInsnFactory(final VarInsnNode insn) {
        super(insn);
    }

    public VarInsnFactory(final int opcode, final int var) {
        this(new VarInsnNode(opcode, var));
    }

    public VarInsnFactory var(final int newVar) {
        return super.deploy(() -> {
            super.getInstruction().var = newVar;
            return this;
        });
    }

    public VarInsnFactory opcode(final int newOpcode) {
        return super.deploy(() -> {
            super.getInstruction().setOpcode(newOpcode);
            return this;
        });
    }
}
