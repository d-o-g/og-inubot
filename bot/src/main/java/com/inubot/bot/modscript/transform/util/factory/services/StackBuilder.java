/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util.factory.services;

import com.inubot.bot.modscript.transform.util.factory.AbstractInsnFactory;
import jdk.internal.org.objectweb.asm.tree.*;
import com.inubot.bot.modscript.transform.util.factory.AbstractInsnFactory;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class StackBuilder {

    private final InsnList list;

    private StackBuilder(final InsnList list) {
        this.list = list;
    }

    private StackBuilder(final AbstractInsnNode... base) {
        final InsnList stack = new InsnList();
        for (final AbstractInsnNode ain : base)
            stack.add(ain);
        this.list = stack;
    }

    public static StackBuilder newInstance(final InsnList list) {
        return new StackBuilder(list);
    }

    public static StackBuilder newInstance() {
        return new StackBuilder(new InsnList());
    }

    public static StackBuilder newInstance(final AbstractInsnNode... base) {
        return new StackBuilder(base);
    }

    public final StackBuilder append(final AbstractInsnFactory factory) {
        this.list.add(factory.getInstruction());
        return this;
    }

    public final StackBuilder append(final AbstractInsnNode ain) {
        this.list.add(ain);
        return this;
    }

    public final StackBuilder appendIf(final boolean condition, final AbstractInsnNode ain) {
        if (condition)
            append(ain);
        return this;
    }

    public final StackBuilder appendIf(final boolean condition, final AbstractInsnFactory factory) {
        if (condition)
            append(factory);
        return this;
    }

    public final StackBuilder appendTo(final boolean add, final MethodNode mn) {
        if (add) {
            mn.instructions.add(list);
        } else {
            mn.instructions.insert(list);
        }
        return this;
    }

    public final StackBuilder appendBefore(final int index, final MethodNode mn) {
        if (mn.instructions.size() > index)
            mn.instructions.insertBefore(mn.instructions.get(index), list);
        return this;
    }

    public final StackBuilder append(final int index, final MethodNode mn) {
        if (mn.instructions.size() > index)
            mn.instructions.insert(mn.instructions.get(index), list);
        return this;
    }

    public final StackBuilder appendTo(final MethodNode mn) {
        return appendTo(true, mn);
    }

    //should this return a StackBuilder instance instead?
    public final MethodNode set(final MethodNode mn) {
        mn.instructions = list;
        return mn;
    }

    public final StackBuilder append(final InsnList list) {
        this.list.add(list);
        return this;
    }

    public final StackBuilder append(final StackBuilder builder) {
        return append(builder.getList());
    }

    public final InsnList getList() {
        return list;
    }
}

