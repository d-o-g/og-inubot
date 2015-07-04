/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform;

import com.inubot.Inubot;
import com.inubot.api.methods.Client;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author unsigned
 * @since 02-05-2015
 */
public class LandscapeHack implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassStructure landscape = classes.get(ModScript.getClass("Region"));
        if (landscape == null)
            throw new RuntimeException("wat");
        for (MethodNode mn : landscape.getMethods(m -> m.desc.startsWith("(IIIIII")
                && m.desc.endsWith("V")
                && Modifier.isPublic(m.access))) {
            InsnList setStack = new InsnList();
            Label label = new Label();
            LabelNode ln = new LabelNode(label);
            mn.visitLabel(label);
            setStack.add(new InsnNode(ICONST_0));
            setStack.add(new FieldInsnNode(GETSTATIC, Client.class.getName().replace('.', '/'), "LANDSCAPE_RENDERING_ENABLED", "Z"));
            setStack.add(new JumpInsnNode(IFNE, ln));
            setStack.add(new InsnNode(RETURN));
            setStack.add(ln);
            mn.instructions.insert(setStack);
        }
    }
}
