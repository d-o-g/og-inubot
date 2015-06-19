/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util.factory;

import jdk.internal.org.objectweb.asm.tree.InsnNode;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class InsnFactory extends AbstractInsnFactory<InsnNode, InsnFactory> {

    public InsnFactory(final int opcode) {
        super(new InsnNode(opcode));
    }
}
