package org.runedream.bot.modscript.transform;

import org.runedream.bot.modscript.ModScript;
import jdk.internal.org.objectweb.asm.tree.*;
import org.runedream.bot.modscript.asm.ClassStructure;

import java.util.Map;

public class CanvasHack implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassNode cn = classes.get(ModScript.getClass("Canvas"));
        cn.superName = "org/runedream/client/GameCanvas";
        nig:
        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals("<init>"))
                continue;
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain instanceof MethodInsnNode) {
                    MethodInsnNode meth = (MethodInsnNode) ain;
                    if (!meth.owner.contains("Canvas"))
                        continue;
                    meth.owner = "org/runedream/client/GameCanvas";
                    break nig;
                }
            }
        }
    }
}
