package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.hooks.InvokeHook;
import jdk.internal.org.objectweb.asm.Type;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.hooks.InvokeHook;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.Map;

public class ProcessActionInvoker implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        InvokeHook meth = ModScript.getInvokeHook("Client#processAction");
        if (meth == null)
            throw new RuntimeException("#processAction hook broke?");
        MethodNode invoker = new MethodNode(ACC_PUBLIC, "processAction", "(IIIILjava/lang/String;Ljava/lang/String;II)V", null, null);
        InsnList stack = new InsnList();
        stack.add(new VarInsnNode(ILOAD, 1));
        stack.add(new VarInsnNode(ILOAD, 2));
        stack.add(new VarInsnNode(ILOAD, 3));
        stack.add(new VarInsnNode(ILOAD, 4));
        stack.add(new VarInsnNode(ALOAD, 5));
        stack.add(new VarInsnNode(ALOAD, 6));
        stack.add(new VarInsnNode(ILOAD, 7));
        stack.add(new VarInsnNode(ILOAD, 8));
        if (meth.predicate != Integer.MAX_VALUE && Type.getArgumentTypes(meth.desc).length > 8)
            stack.add(new LdcInsnNode(meth.predicate));
        stack.add(new MethodInsnNode(INVOKESTATIC, meth.clazz, meth.method, meth.desc, false));
        stack.add(new InsnNode(RETURN));
        invoker.instructions = stack;
        classes.get("client").methods.add(invoker);
        System.out.println("...Injected processAction invoker @" + meth.clazz + "." + meth.method + meth.desc + "!");
    }
}
