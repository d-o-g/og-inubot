/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util.factory.services;

import com.inubot.bot.modscript.transform.util.factory.FactoryAgent;
import com.inubot.bot.modscript.transform.util.factory.FieldInsnFactory;
import com.inubot.bot.modscript.transform.util.factory.InsnFactory;
import com.inubot.bot.modscript.transform.util.factory.VarInsnFactory;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class FieldGetterFactory implements Opcodes {

    private FieldGetterFactory() {

    }

    public static FactoryAgent<MethodNode> supply(final int access, final String name, final String desc,
                                                  final String signature, final String[] exceptions, final FieldInsnNode return_) {
        final MethodNode mn = new MethodNode(access, name, desc, signature, exceptions);
        return () -> StackBuilder.newInstance()
                .appendIf(Modifier.isStatic(access), new VarInsnFactory(ALOAD, 0))
                .append(return_)
                .append(new InsnFactory(Type.getReturnType(desc).getOpcode(IRETURN)))
                .set(mn);
    }

    public static FactoryAgent<MethodNode> supply(final int access, final String name, final String desc,
                                                  final String signature, final String[] exceptions, final FieldInsnFactory return_) {
        return supply(access, name, desc, signature, exceptions, return_.getInstruction());
    }

    public static FactoryAgent<MethodNode> supply(final MethodNode mn, final FieldInsnNode return_) {
        final String[] exceptions = new String[mn.exceptions.size()];
        mn.exceptions.toArray(exceptions);
        return supply(mn.access, mn.name, mn.desc, mn.signature, exceptions, return_);
    }

    public static FactoryAgent<MethodNode> supply(final MethodNode mn, final FieldInsnFactory return_) {
        final String[] exceptions = new String[mn.exceptions.size()];
        mn.exceptions.toArray(exceptions);
        return supply(mn.access, mn.name, mn.desc, mn.signature, exceptions, return_);
    }
}
