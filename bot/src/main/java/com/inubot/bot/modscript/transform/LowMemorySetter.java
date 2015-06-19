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
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * @author unsigned
 * @since 09-05-2015
 */
public class LowMemorySetter implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        MethodNode mn = new MethodNode(ACC_PUBLIC, "setLowMemory", "(Z)V", null, null);
        mn.instructions.add(new VarInsnNode(ILOAD, 1));
        FieldHook hook = ModScript.getFieldHook("Client#lowMemory");
        mn.instructions.add(new FieldInsnNode(PUTSTATIC, hook.clazz, hook.field, hook.fieldDesc));
        mn.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(mn);
    }
}
