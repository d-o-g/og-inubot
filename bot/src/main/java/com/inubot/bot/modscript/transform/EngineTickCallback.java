/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.client.Callback;
import jdk.internal.org.objectweb.asm.tree.*;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.client.Callback;

import java.util.Map;

/**
 * @author unsigned
 * @since 02-05-2015
 */
public class EngineTickCallback implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassStructure engine = classes.get(classes.get("client").superName);
        for (MethodNode run : engine.methods) {
            if (!run.name.equals("run") || !run.desc.equals("()V"))
                continue;
            for (AbstractInsnNode ain : run.instructions.toArray()) {
                if (ain.getOpcode() == PUTSTATIC && backtrack(ain, INVOKEVIRTUAL)) {
                    System.out.println("OK THIS NIGGER IS A PERSON TOO, LEAVE HIM ALOEN");
                    run.instructions.insert(ain, new MethodInsnNode(INVOKESTATIC, Callback.class.getName().replace('.', '/'),
                            "onEngineTick", "()V", false));
                }
            }
        }
    }

    private boolean backtrack(AbstractInsnNode ain, int insn) {
        for (int i = 0; i < 5 && (ain = ain.getPrevious()) != null; i++) {
            if (ain.getOpcode() == insn)
                return true;
        }
        return false;
    }
}
