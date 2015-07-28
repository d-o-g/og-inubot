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
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;

import java.math.BigInteger;
import java.util.Map;

/**
 * @author Dogerina
 * @since 28-07-2015
 */
public class MapRotationSetter implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        FieldHook hook = ModScript.getFieldHook("Client#mapRotation");
        if (hook == null) {
            throw new IllegalStateException("rotation_hook_broken");
        }
        ClassStructure struc = classes.get("client");
        MethodNode setter = new MethodNode(ACC_PUBLIC, "setMapRotation", "(I)V", null, null);
        InsnList stack = new InsnList();
        stack.add(new VarInsnNode(Type.getType(hook.fieldDesc).getOpcode(Opcodes.ILOAD), 1));
        stack.add(new FieldInsnNode(PUTSTATIC, hook.clazz, hook.field, hook.fieldDesc));
        stack.add(new LdcInsnNode(getInverse(hook.multiplier)));
        stack.add(new InsnNode(Opcodes.IMUL));
        stack.add(new InsnNode(Opcodes.RETURN));
        setter.instructions.add(stack);
        struc.methods.add(setter);
    }

    private int getInverse(int num) {
        return BigInteger.valueOf(num).modInverse(new BigInteger(String.valueOf(1L << 32))).intValue();
    }
}
