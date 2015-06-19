/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.bot.modscript.transform.util.factory;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class IntInsnFactory extends AbstractInsnFactory<IntInsnNode, IntInsnFactory> {

    public IntInsnFactory(IntInsnNode insn) {
        super(insn);
    }

    public IntInsnFactory(final int opcode, final int operand) {
        this(new IntInsnNode(opcode, operand));
    }

    public final IntInsnFactory operand(final int newOperand) {
        return super.deploy(() -> {
            super.getInstruction().operand = newOperand;
            return this;
        });
    }

    public final IntInsnFactory opcode(final int newOpcode) {
        return super.deploy(() -> {
            super.getInstruction().setOpcode(newOpcode);
            return this;
        });
    }
}