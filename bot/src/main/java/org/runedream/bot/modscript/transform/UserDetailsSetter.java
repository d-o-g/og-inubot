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
 * @since 29-04-2015
 */
public class UserDetailsSetter implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassNode client = classes.get("client");
        client.methods.add(mkStringSetter("setUsername", ModScript.getFieldHook("Client#username")));
        client.methods.add(mkStringSetter("setPassword", ModScript.getFieldHook("Client#password")));
    }

    private MethodNode mkStringSetter(String name, FieldHook hook) {
        MethodNode meth = new MethodNode(ACC_PUBLIC, name, "(Ljava/lang/String;)V", null, null);
        meth.instructions.add(new VarInsnNode(ALOAD, 1));
        meth.instructions.add(new FieldInsnNode(PUTSTATIC, hook.clazz, hook.field, "Ljava/lang/String;"));
        meth.instructions.add(new InsnNode(RETURN));
        return meth;
    }
}
