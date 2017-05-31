package com.inubot.bot.modscript.transform;

import com.inubot.api.methods.Client;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.hooks.FieldHook;
import com.inubot.bot.modscript.transform.util.ASMFactory;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.Map;

public class WidgetHack implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassNode widget = classes.get(ModScript.getClass("InterfaceComponent"));
        widget.fields.add(new FieldNode(ACC_PUBLIC, "containerX", "I", null, null));
        widget.fields.add(new FieldNode(ACC_PUBLIC, "containerY", "I", null, null));
        widget.methods.add(ASMFactory.createGetter(false, widget.name, "containerX", "I", "containerX"));
        widget.methods.add(ASMFactory.createGetter(false, widget.name, "containerY", "I", "containerY"));
        FieldHook x = ModScript.getFieldHook("InterfaceComponent#x");
        FieldHook y = ModScript.getFieldHook("InterfaceComponent#y");

        /*
           int var13 = var11.relativeX * 280533711 + var6; //absoluteX
           int var14 = var11.relativeY * 150978763 + var7; //absoluteY
           //set here
           ............................
           var13 = var16;
           var14 = var17;
           //and here
         */
        for (ClassNode node : classes.values()) {
            for (MethodNode mn : node.methods) {
                if (!Modifier.isStatic(mn.access) || !mn.desc.endsWith("V")
                        || !mn.desc.startsWith("([L" + widget.name + ";IIIIII"))
                    continue;
                InsnList setStack = new InsnList();
                Label label = new Label();
                LabelNode ln = new LabelNode(label);
                mn.visitLabel(label);
                setStack.add(new InsnNode(ICONST_0));
                setStack.add(new FieldInsnNode(GETSTATIC, Client.class.getName().replace('.', '/'), "INTERFACE_RENDERING_ENABLED", "Z"));
                setStack.add(new JumpInsnNode(IFNE, ln));
                setStack.add(new InsnNode(RETURN));
                setStack.add(ln);
                mn.instructions.insert(setStack);
                //Phase 1
                int obj = -1, ilx = -1, ily = -1;
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() != GETFIELD || ain.getPrevious() == null || ain.getPrevious().getOpcode() != ALOAD)
                        continue;
                    VarInsnNode aload = (VarInsnNode) ain.getPrevious();
                    FieldInsnNode fin = (FieldInsnNode) ain;
                    if (fin.owner.equals(widget.name) && (prev(fin, IADD, 7) != null || next(fin, IADD, 7) != null)) {
                        if (fin.name.equals(x.field)) {
                            VarInsnNode set = (VarInsnNode) next(fin, ISTORE, 7);
                            if (set != null) {
                                InsnList stack = new InsnList();
                                stack.add(new VarInsnNode(ALOAD, aload.var));
                                stack.add(new VarInsnNode(ILOAD, set.var));
                                stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerX", "I"));
                                mn.instructions.insert(set, stack);
                                ilx = set.var;
                                obj = aload.var;
                            }
                        } else if (fin.name.equals(y.field)) {
                            VarInsnNode set = (VarInsnNode) next(fin, ISTORE, 7);
                            if (set != null) {
                                InsnList stack = new InsnList();
                                stack.add(new VarInsnNode(ALOAD, aload.var));
                                stack.add(new VarInsnNode(ILOAD, set.var));
                                stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerY", "I"));
                                mn.instructions.insert(set, stack);
                                ily = set.var;
                            }
                        }
                    }
                }
                if (obj == -1 || ilx == -1 || ily == -1)
                    continue;
                //Phase 2
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() != ISTORE)
                        continue;
                    VarInsnNode store = (VarInsnNode) ain;
                    if (store.var == ily) {
                        InsnList stack = new InsnList();
                        stack.add(new VarInsnNode(ALOAD, obj));
                        stack.add(new VarInsnNode(ILOAD, ilx));
                        stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerX", "I"));
                        stack.add(new VarInsnNode(ALOAD, obj));
                        stack.add(new VarInsnNode(ILOAD, ily));
                        stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerY", "I"));
                        mn.instructions.insert(store, stack);
                    }
                }
            }
        }
    }

    private boolean match(final FieldHook mh, FieldInsnNode fin) {
        return fin != null && fin.owner.equals(mh.clazz) && fin.name.equals(mh.field) && fin.desc.equals(mh.fieldDesc);
    }

    private AbstractInsnNode next(AbstractInsnNode curr, int op, int dist) {
        AbstractInsnNode ok = curr;
        for (int i = 0; i < dist && (ok = ok.getNext()) != null; i++) {
            if (ok.getOpcode() == op)
                return ok;
        }
        return null;
    }

    private AbstractInsnNode prev(AbstractInsnNode curr, int op, int dist) {
        AbstractInsnNode ok = curr;
        for (int i = 0; i < dist && (ok = ok.getPrevious()) != null; i++) {
            if (ok.getOpcode() == op)
                return ok;
        }
        return null;
    }
}
