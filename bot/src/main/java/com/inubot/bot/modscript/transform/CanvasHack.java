package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class CanvasHack implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassNode cn = classes.get(ModScript.getClass("Canvas"));
        cn.superName = "com/inubot/client/GameCanvas";
        nig:
        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals("<init>"))
                continue;
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain instanceof MethodInsnNode) {
                    MethodInsnNode meth = (MethodInsnNode) ain;
                    if (!meth.owner.contains("Canvas"))
                        continue;
                    meth.owner = "com/inubot/client/GameCanvas";
                    break nig;
                }
            }
        }
    }
}
