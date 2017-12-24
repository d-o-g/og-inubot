package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.hooks.FieldHook;
import com.inubot.bot.modscript.hooks.InvokeHook;
import com.inubot.client.Callback;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by Asus on 25/05/2017.
 */
public class GraphicsProducer implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        String ch = ModScript.getClass("Producer");
        InvokeHook ih = ModScript.getInvokeHook("Producer#drawGame");
        FieldHook redraw = ModScript.getFieldHook("Client#redrawMode");
        for (ClassStructure cn : classes.values()) {
            if (cn.name.equals(ch)) {
                FieldNode img = null;
                for (FieldNode fn : cn.fields) {
                    if (!Modifier.isStatic(fn.access) && fn.desc.contains("Image")) {
                        img = fn;
                        break;
                    }
                }
                for (MethodNode mn : cn.methods) {

                    if (ih.method.equalsIgnoreCase(mn.name) && ih.desc.equalsIgnoreCase(mn.desc)) {
                        InsnList stack = new InsnList();
                        stack.add(new VarInsnNode(ALOAD, 0));
                        stack.add(new FieldInsnNode(GETFIELD, cn.name, img.name, img.desc));
                        stack.add(new MethodInsnNode(INVOKESTATIC, Callback.class.getName().replace('.', '/'),
                                "draw", "(Ljava/awt/Image;)V", false));
                        mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
                    }
                }
            }
            if (cn.name.equalsIgnoreCase("client")) {
                for (MethodNode mn : cn.methods) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == PUTSTATIC) {
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if (fin.owner.equals(redraw.clazz) && fin.name.equals(redraw.field)) {
                                AbstractInsnNode prev = fin.getPrevious();
                                mn.instructions.set(prev, new InsnNode(ICONST_1));
                            }
                        }
                    }
                }
            }
        }
    }
}
