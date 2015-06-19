/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.bot.modscript.transform;

import jdk.internal.org.objectweb.asm.tree.*;
import org.runedream.bot.modscript.ModScript;
import org.runedream.bot.modscript.asm.ClassStructure;
import org.runedream.bot.modscript.hooks.FieldHook;

import java.util.Map;

/**
 * @author unsigned
 * @since 22-04-2015
 */
public class IdleTimeSetter implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        FieldHook hook = ModScript.getFieldHook("Client#mouseIdleTime");
        if (hook == null)
            throw new RuntimeException("hook broke?");
        MethodNode setter = new MethodNode(ACC_PUBLIC, "resetMouseIdleTime", "()V", null, null);
        setter.instructions.add(new InsnNode(ICONST_0));
        setter.instructions.add(new FieldInsnNode(PUTSTATIC, hook.clazz, hook.field, hook.fieldDesc));
        setter.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(setter);
    }
}
