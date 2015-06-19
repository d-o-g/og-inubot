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
import com.inubot.bot.modscript.hooks.InvokeHook;
import com.inubot.client.natives.RSVarpBit;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public class VarpBitHack implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        InvokeHook vb = ModScript.getInvokeHook("Client#getVarpBit");
        InsnList modded = null;
        if (!vb.desc.endsWith("L" + ModScript.getClass("VarpBit") + ";")) { //the hook didn't return the varpbit
            for (final MethodNode mn : classes.get(vb.clazz).methods) {
                if (!mn.name.equals(vb.method) || !mn.desc.equals(vb.desc))
                    continue;
                MethodNode clone = new MethodNode();
                clone.tryCatchBlocks = new ArrayList<>();
                mn.accept(clone);
                modded = mod(clone.instructions, ModScript.getFieldHook("VarpBit#varpIndex"));
                //mn.desc = "(I)L" + Type.getInternalName(RSVarpBit.class) + ";";
            }
        } else { //inject regular invoker
            MethodNode test = new MethodNode(ACC_PUBLIC, "getVarpBit", "(I)L" + Type.getInternalName(RSVarpBit.class) + ";", null, null);
            classes.get("client").methods.add(test);
            test.instructions.add(new VarInsnNode(Opcodes.ILOAD, 1));
            test.instructions.add(new LdcInsnNode(vb.predicate));
            test.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, vb.clazz, vb.method, vb.desc, false));
            test.instructions.add(new InsnNode(Opcodes.ARETURN));
        }
        if (modded == null) return;
        MethodNode varp0 = new MethodNode(ACC_PUBLIC | ACC_STATIC, "getVarpBit0", "(II)L" + Type.getInternalName(RSVarpBit.class)
                + ";", null, null);
        varp0.instructions = modded;

        for (final MethodNode mn : classes.get("client").methods) {
            if (!mn.name.equals("getVarpBit")) {
                continue;
            }
            String desc = mn.desc.substring(0, mn.desc.length() - 1);
            desc += "L" + Type.getInternalName(RSVarpBit.class) + ";";
            mn.desc = desc;

            InsnList stack = new InsnList();
            stack.add(new VarInsnNode(Opcodes.ILOAD, 1));
            stack.add(new LdcInsnNode(vb.predicate));
            stack.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "client", varp0.name, varp0.desc, false));
            stack.add(new InsnNode(Opcodes.ARETURN));
            mn.instructions = stack;
        }
        classes.get("client").methods.add(varp0);
    }

    private InsnList mod(final InsnList instructions, FieldHook varpIndex) { //TODO remove bitwise calc after the return?
        InsnList dumystack = new InsnList();
        dumystack.add(new LdcInsnNode(ModScript.getInvokeHook("Client#getVarpBit").predicate)); //dummy
        dumystack.add(new VarInsnNode(Opcodes.ISTORE, 2));
        instructions.insertBefore(instructions.getFirst(), dumystack);
        //find astore variable index for Varpbit variable
        //find and remove:

        int varb = -1;

        for (AbstractInsnNode ain : instructions.toArray()) {
            if (ain.getOpcode() != Opcodes.GETFIELD)
                continue;
            FieldInsnNode fin = (FieldInsnNode) ain;
            if (!varpIndex.clazz.equals(fin.owner) || !varpIndex.field.equals(fin.name))
                continue;
            VarInsnNode varpbit = (VarInsnNode) ain.getPrevious();
            varb = varpbit.var;
            break;
        }

        for (AbstractInsnNode ain : instructions.toArray()) {
            if (ain.getOpcode() == Opcodes.IRETURN) {
                InsnList stack = new InsnList();
                stack.add(new InsnNode(Opcodes.POP));
                stack.add(new VarInsnNode(Opcodes.ALOAD, varb));
                stack.add(new InsnNode(Opcodes.ARETURN));
                instructions.insertBefore(ain, stack);
                stack.remove(ain);
            }
        }
        return instructions;
    }
}
