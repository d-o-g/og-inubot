package org.runedream.bot.modscript.transform;

import org.runedream.bot.modscript.asm.ClassStructure;
import org.runedream.client.Callback;
import org.runedream.bot.modscript.ModScript;
import org.runedream.bot.modscript.hooks.InvokeHook;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class ProcessActionCallback implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        InvokeHook meth = ModScript.getInvokeHook("Client#processAction");
        if (meth == null)
            throw new RuntimeException("#processAction hook broke?");
        for (ClassNode cn : classes.values()) {
            if (!cn.name.equals(meth.clazz))
                continue;
            for (MethodNode mn : cn.methods) {
                if (!mn.name.equals(meth.method) || !mn.desc.equals(meth.desc))
                    continue;
                InsnList stack = new InsnList();
                stack.add(new VarInsnNode(ILOAD, 0));
                stack.add(new VarInsnNode(ILOAD, 1));
                stack.add(new VarInsnNode(ILOAD, 2));
                stack.add(new VarInsnNode(ILOAD, 3));
                stack.add(new VarInsnNode(ALOAD, 4));
                stack.add(new VarInsnNode(ALOAD, 5));
                stack.add(new VarInsnNode(ILOAD, 6));
                stack.add(new VarInsnNode(ILOAD, 7));
                stack.add(new MethodInsnNode(INVOKESTATIC, Callback.class.getName().replace('.', '/'), "processAction",
                        "(IIIILjava/lang/String;Ljava/lang/String;II)V", false));
                mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
                System.out.println("...Injected processAction callback @" + cn.name + "." + mn.name + mn.desc + "!");
            }
        }
    }
}
