package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.IntInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

import java.util.Map;

/**
 * Created by Asus on 25/05/2017.
 */
public class ProducerForceBounds implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        for (ClassStructure cn : classes.values()) {
            if (cn.name.equalsIgnoreCase(ModScript.getClass("Producer"))) {
                for (MethodNode mn : cn.methods) {
                    if (mn.name.equals("<init>")) {
                        InsnList set = new InsnList();
                        set.add(new IntInsnNode(SIPUSH, 765));
                        set.add(new VarInsnNode(ASTORE, 1));
                        set.add(new IntInsnNode(SIPUSH, 503));
                        set.add(new VarInsnNode(ASTORE, 2));
                        mn.instructions.insertBefore(mn.instructions.getFirst(), set);
                        System.out.println("injd");
                    }
                }
            }
        }
    }
}
