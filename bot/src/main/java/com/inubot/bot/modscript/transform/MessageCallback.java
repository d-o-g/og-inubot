/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.tree.*;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.client.Callback;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class MessageCallback implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        for (final ClassNode cn : classes.values()) {
            for (final MethodNode mn : cn.methods) {
                if (!Modifier.isStatic(mn.access) || !mn.desc.endsWith("V")
                        || !mn.desc.startsWith("(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;")) {
                    continue;
                }
                final InsnList list = new InsnList();
                list.add(new VarInsnNode(ILOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new MethodInsnNode(INVOKESTATIC, Callback.class.getName().replace('.', '/'),
                        "messageReceived", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false));
                mn.instructions.insertBefore(mn.instructions.getFirst(), list);
                System.out.println("Injected message @ " + cn.name + "." + mn.name + mn.desc);
            }
        }
    }
}
