/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util;

import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author unsigned
 * @since 25-04-2015
 */
public class InstructionIterator {

    private final InsnList stack;

    public InstructionIterator(InsnList stack) {
        this.stack = stack;
    }

    public void accept(Predicate<AbstractInsnNode> match, Consumer<AbstractInsnNode> consumer) {
        for (AbstractInsnNode ain : stack.toArray()) {
            if (!match.test(ain))
                continue;
            consumer.accept(ain);
        }
    }
}
