/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util.factory;

import jdk.internal.org.objectweb.asm.tree.LdcInsnNode;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class LdcInsnFactory extends AbstractInsnFactory<LdcInsnNode, LdcInsnFactory> {

    public LdcInsnFactory(final LdcInsnNode insn) {
        super(insn);
    }

    public LdcInsnFactory(final Object cst) {
        super(new LdcInsnNode(cst));
    }

    public final LdcInsnFactory constant(final Object cst) {
        return super.deploy(() -> {
            super.getInstruction().cst = cst;
            return this;
        });
    }
}
