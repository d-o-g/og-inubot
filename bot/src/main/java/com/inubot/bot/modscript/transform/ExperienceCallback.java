/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.hooks.FieldHook;
import com.inubot.client.Callback;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author Dogerina
 * @since 17-07-2015
 */
public class ExperienceCallback implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        FieldHook exphook = ModScript.getFieldHook("Client#experiences");
        if (exphook == null)
            throw new IllegalStateException("Experiences hook broke?");
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                if (Modifier.isStatic(mn.access) && Type.getArgumentTypes(mn.desc).length <= 2) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() != GETSTATIC)
                            continue;
                        FieldInsnNode ref = (FieldInsnNode) ain;
                        if (!ref.owner.equals(exphook.clazz) || !ref.name.equals(exphook.field))
                            continue;
                        if (ref.getNext() != null && ref.getNext().getNext() != null &&
                                ref.getNext().getNext().getNext() != null
                                && ref.getNext().getNext().getNext().getOpcode() == IASTORE) {
                            VarInsnNode valueref = (VarInsnNode) ref.getNext();
                            VarInsnNode indexref = (VarInsnNode) ref.getNext().getNext();
                            InsnList stack = new InsnList();
                            stack.add(new VarInsnNode(ILOAD, valueref.var));
                            stack.add(new VarInsnNode(ILOAD, indexref.var));
                            stack.add(new MethodInsnNode(INVOKESTATIC, Callback.class.getName().replace('.', '/'),
                                    "experienceGain", "(II)V", false));
                            mn.instructions.insertBefore(ref, stack);
                            //insert before the arraystore ^
                        }
                    }
                }
            }
        }
    }
}
