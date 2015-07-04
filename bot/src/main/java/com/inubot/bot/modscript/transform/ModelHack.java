package com.inubot.bot.modscript.transform;

import com.inubot.Inubot;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.tree.*;
import com.inubot.api.methods.Client;

import java.lang.reflect.Modifier;
import java.util.*;

public class ModelHack implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassNode model = classes.get(ModScript.getClass("Model"));
        List<String> badKeys = new ArrayList<>(); //TODO identify the method in updater module
        for (MethodNode mn : model.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                //onCursorUids[onCursorCount++] = ...;
                if (ain.getOpcode() != IASTORE || !matchPrevs(ain, ILOAD, PUTSTATIC, IADD, ICONST_1, DUP, GETSTATIC, GETSTATIC))
                    continue;
                badKeys.add(mn.name + mn.desc);
            }
        }
        for (MethodNode mn : model.methods) {
            if (badKeys.contains(mn.name + mn.desc) || mn.desc.contains("[B") || Modifier.isStatic(mn.access))
                continue;
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getOpcode() != IFNONNULL || !matchPrevs(ain, GETFIELD, ALOAD))
                    continue;
                FieldInsnNode field = (FieldInsnNode) ain.getPrevious();
                if (field.desc.equals("[B")) {
                    VarInsnNode aload = (VarInsnNode) ain.getPrevious().getPrevious();
                    InsnList setStack = new InsnList();
                    Label label = new Label();
                    LabelNode ln = new LabelNode(label);
                    mn.visitLabel(label);
                    setStack.add(new InsnNode(ICONST_0));
                    setStack.add(new FieldInsnNode(GETSTATIC, Client.class.getName().replace('.', '/'), "MODEL_RENDERING_ENABLED", "Z"));
                    setStack.add(new JumpInsnNode(IFNE, ln));
                    setStack.add(new InsnNode(RETURN));
                    setStack.add(ln);
                    mn.instructions.insertBefore(aload, setStack);
                }
            }
        }
    }

    private boolean matchPrevs(AbstractInsnNode ain, int... ops) {
        AbstractInsnNode curr = ain;
        for (int i = 0; i < ops.length && (curr = curr.getPrevious()) != null; i++) {
            if (curr.getOpcode() != ops[i])
                return false;
        }
        return true;
    }
}
