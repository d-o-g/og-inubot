/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.bot.modscript.transform.util.factory.services;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;
import org.runedream.bot.modscript.transform.util.factory.*;

import java.lang.reflect.Modifier;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class MethodInvokerFactory implements Opcodes {

    private MethodInvokerFactory() {

    }

    /**
     * @param access     The access of the MethodNode to create
   //  * @param owner      The owner of the MethodNode
     * @param name       The method name
     * @param desc       The desc of the method
     * @param signature  The signature (generics)
     * @param exceptions The exceptions of this MethodNode
     * @param call       The object that the method should return
     * @param args       The args to pass to the call
     * @return A FactoryAgent which is able to supply the desired invoker MethodNode
     */
    public static FactoryAgent<MethodNode> supply(final int access, final String name, final String desc, final String signature,
                                                  final String[] exceptions, final MethodInsnNode call,
                                                  final AbstractInsnNode... args) {
        final MethodNode mn = new MethodNode(access, name, desc, signature, exceptions);
        return () -> StackBuilder.newInstance()
                .appendIf(Modifier.isStatic(access), new VarInsnFactory(ALOAD, 0))
                .append(StackBuilder.newInstance(args))
                .append(call)
                .append(new InsnFactory(Type.getReturnType(desc).getOpcode(IRETURN)))
                .set(mn);
    }

    public static FactoryAgent<MethodNode> supply(final int access, final String name, final String desc, final String signature,
                                                  final String[] exceptions, final MethodInsnFactory call,
                                                  final AbstractInsnNode... args) {
        return supply(access, name, desc, signature, exceptions, call.getInstruction(), args);
    }

    public static FactoryAgent<MethodNode> supply(final MethodNode mn, final MethodInsnNode call, final AbstractInsnNode... args) {
        final String[] exceptions = new String[mn.exceptions.size()];
        mn.exceptions.toArray(exceptions);
        return supply(mn.access, mn.name, mn.desc, mn.signature, exceptions, call, args);
    }

    public static FactoryAgent<MethodNode> supply(final MethodNode mn, final MethodInsnFactory call, final AbstractInsnNode... args) {
        final String[] exceptions = new String[mn.exceptions.size()];
        mn.exceptions.toArray(exceptions);
        return supply(mn.access, mn.name, mn.desc, mn.signature, exceptions, call, args);
    }
}

