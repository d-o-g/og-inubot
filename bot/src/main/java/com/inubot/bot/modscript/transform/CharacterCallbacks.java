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
import com.inubot.client.listener.HealthListener;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.*;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.hooks.FieldHook;
import com.inubot.client.listener.HealthListener;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author unsigned
 * @since 23-05-2015
 */
public class CharacterCallbacks implements Transform {

    private static int getOpenVar(MethodNode mn) {
        int maxVar = Type.getArgumentTypes(mn.desc).length;
        if (!Modifier.isStatic(mn.access))
            maxVar++; //startvar = 1
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof VarInsnNode) {
                VarInsnNode vin = (VarInsnNode) ain;
                if (vin.var > maxVar)
                    maxVar = vin.var;
            }
        }
        return ++maxVar;
    }

    //can do targetIndex, overheadText, healthBarCycle and animation here too, cbf tho only need health for now
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        //add field
        ClassStructure character = classes.get(ModScript.getClass("Character"));
        if (character == null)
            throw new RuntimeException("Character broked.");
        FieldNode hl = new FieldNode(ACC_PUBLIC, "healthListener", "L" + HealthListener.class.getName().replace('.', '/') + ";", null, null);
        character.fields.add(hl);
        //add getter
        MethodNode hlgetter = new MethodNode(ACC_PUBLIC, "getHealthListener", "()L" + HealthListener.class.getName().replace('.', '/') + ";",
                null, null);
        hlgetter.instructions.add(new VarInsnNode(ALOAD, 0));
        hlgetter.instructions.add(new FieldInsnNode(GETFIELD, character.name, hl.name, hl.desc));
        hlgetter.instructions.add(new InsnNode(ARETURN));
        character.methods.add(hlgetter);
        //add setter
        MethodNode hlsetter = new MethodNode(ACC_PUBLIC, "setHealthListener", "(L" + HealthListener.class.getName().replace('.', '/') + ";)V",
                null, null);
        hlsetter.instructions.add(new VarInsnNode(ALOAD, 1));
        hlsetter.instructions.add(new VarInsnNode(ALOAD, 0));
        hlsetter.instructions.add(new FieldInsnNode(PUTFIELD, character.name, hl.name, hl.desc));
        hlsetter.instructions.add(new InsnNode(RETURN));
        character.methods.add(hlsetter);
        //inject call to healthlistener#onchange
        FieldHook health = ModScript.getFieldHook("Character#health");
        if (health == null)
            throw new RuntimeException("Health broked.");
        String npc = ModScript.getClass("Npc"), player = ModScript.getClass("Player");
        if (npc == null || player == null)
            throw new RuntimeException("CharacterSub broked.");
        for (ClassStructure cs : classes.values()) {
            for (MethodNode mn : cs.methods) {
                int nextVar = -1;
                if (mn.name.contains("<"))
                    continue;
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == PUTFIELD) {
                        FieldInsnNode fin = (FieldInsnNode) ain;
                        if ((fin.owner.equals(player) || fin.owner.equals(npc) || fin.owner.equals(character.name))
                                && fin.name.equals(health.field)) {
                            if (nextVar == -1)
                                nextVar = getOpenVar(mn);
                            LabelNode exitFlow = new LabelNode(new Label());
                            mn.visitLabel(exitFlow.getLabel());
                            //After the field is set, the referenced object and its value will be left on the stack
                            //REF
                            //PREV_VALUE
                            int oldValRef = nextVar++;
                            int cref = nextVar++;
                            InsnList pstack = new InsnList();
                            pstack.add(new InsnNode(DUP2));
                            pstack.add(new InsnNode(POP));
                            pstack.add(new VarInsnNode(ASTORE, cref));
                            pstack.add(new VarInsnNode(ALOAD, cref));
                            pstack.add(new FieldInsnNode(GETFIELD, health.clazz, health.field, health.fieldDesc));
                            pstack.add(new LdcInsnNode(health.multiplier));
                            pstack.add(new InsnNode(IMUL));
                            mn.instructions.insertBefore(fin, pstack);
                            InsnList stack = new InsnList();
                            //if (listener == null) -> A
                            stack.add(new VarInsnNode(ALOAD, cref));
                            stack.add(new FieldInsnNode(GETFIELD, character.name, "healthListener", "L" + HealthListener.class.getName().replace('.', '/') + ";"));
                            stack.add(new JumpInsnNode(IFNONNULL, exitFlow)); // If the listener is null, skip listener notifications :: PREV_VALUE + B

                            //if (var20.healthListener != null) {
                            //   var20.healthListener.onChange(old, new);
                            //}
                            stack.add(new VarInsnNode(ALOAD, cref));  // C
                            stack.add(new FieldInsnNode(GETFIELD, character.name, "healthListener", "L" + HealthListener.class.getName().replace('.', '/') + ";"));
                            stack.add(new VarInsnNode(ALOAD, cref));
                            stack.add(new MethodInsnNode(INVOKEVIRTUAL, character.name, "getHealth", "()I", false));
                            stack.add(new VarInsnNode(ILOAD, oldValRef));  //  C + PREV_VALUE
                            stack.add(new MethodInsnNode(INVOKEVIRTUAL, HealthListener.class.getName().replace('.', '/'), "onChange", "(II)V", true));
                            stack.add(exitFlow);
                            mn.instructions.insert(ain, stack); //insert before the field is set so we can delegate old value
                            System.out.println("Injected health listener @" + cs.name + "#" + mn.name + mn.desc);
                        }
                    }
                }
            }
        }
    }
}
