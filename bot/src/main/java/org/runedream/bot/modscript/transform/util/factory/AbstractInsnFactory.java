/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.bot.modscript.transform.util.factory;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public abstract class AbstractInsnFactory<T extends AbstractInsnNode, F extends AbstractInsnFactory<T, F>> {

    private final AtomicReference<T> insn;

    public AbstractInsnFactory(final T insn) {
        this.insn = new AtomicReference<>(insn);
    }

    public final F deploy(final FactoryAgent<F> agent) {
        return agent.supply();
    }

    public final F accept(final MethodVisitor mv) {
        return deploy(() -> {
            insn.get().accept(mv);
            return (F) this;
        });
    }

    public T getInstruction() {
        return insn.get();
    }
}
