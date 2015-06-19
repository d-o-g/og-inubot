package org.runedream.bot.modscript.transform;

import jdk.internal.org.objectweb.asm.tree.*;
import org.runedream.bot.modscript.ModScript;
import org.runedream.bot.modscript.asm.ClassStructure;
import org.runedream.bot.modscript.hooks.FieldHook;
import org.runedream.bot.modscript.transform.util.ASMFactory;
import org.runedream.bot.modscript.transform.util.RIS;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

public class WidgetPositionHack implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        ClassNode widget = classes.get(ModScript.getClass("Widget"));
        widget.fields.add(new FieldNode(ACC_PUBLIC, "containerX", "I", null, null));
        widget.fields.add(new FieldNode(ACC_PUBLIC, "containerY", "I", null, null));
        widget.methods.add(ASMFactory.createGetter(false, widget.name, "containerX", "I", "containerX"));
        widget.methods.add(ASMFactory.createGetter(false, widget.name, "containerY", "I", "containerY"));
        FieldHook x = ModScript.getFieldHook("Widget#x");
        FieldHook y = ModScript.getFieldHook("Widget#y");
        final Pattern pattern0 = RIS.mkPattern(GETSTATIC, LDC, GETSTATIC, IMUL, ILOAD, LDC, ALOAD, GETFIELD, IMUL, IADD, IASTORE);
        final Pattern pattern1 = RIS.mkPattern(GETSTATIC, GETSTATIC, LDC, IMUL, ILOAD, LDC, ALOAD, GETFIELD, IMUL, IADD, IASTORE);

        final Pattern pattern2 = RIS.mkPattern(GETSTATIC, LDC, GETSTATIC, IMUL, LDC, ALOAD, GETFIELD, IMUL, ILOAD, IADD, IASTORE);
        final Pattern pattern3 = RIS.mkPattern(GETSTATIC, GETSTATIC, LDC, IMUL, LDC, ALOAD, GETFIELD, IMUL, ILOAD, IADD, IASTORE);

        final Pattern pattern5 = RIS.mkPattern(GETSTATIC, LDC, GETSTATIC, IMUL, ALOAD, GETFIELD, LDC, IMUL, ILOAD, IADD, IASTORE);
        final Pattern pattern4 = RIS.mkPattern(GETSTATIC, GETSTATIC, LDC, IMUL, ALOAD, GETFIELD, LDC, IMUL, ILOAD, IADD, IASTORE);


        //    int var11 = var6 + var10.p * 692928385;

        final Pattern pattern6 = RIS.mkPattern(ILOAD, ALOAD, GETFIELD, LDC, IMUL, IADD, ISTORE);
        final Pattern pattern7 = RIS.mkPattern(ILOAD, LDC, ALOAD, GETFIELD, IMUL, IADD, ISTORE);

        final Pattern pattern8 = RIS.mkPattern(ALOAD, GETFIELD, LDC, IMUL, ILOAD, IADD, ISTORE);
        final Pattern pattern9 = RIS.mkPattern(LDC, ALOAD, GETFIELD, IMUL, ILOAD, IADD, ISTORE);


        for (final ClassNode node : classes.values()) {
            for (final MethodNode mn : node.methods) {
                if (!Modifier.isStatic(mn.access) || !mn.desc.endsWith("V")
                        || !mn.desc.startsWith("([L" + widget.name + ";IIIIII"))
                    continue;
                RIS searcher = new RIS(mn.instructions);
                List<AbstractInsnNode[]> matches = new ArrayList<>();
                matches.addAll(searcher.search(pattern0));
                matches.addAll(searcher.search(pattern1));
                matches.addAll(searcher.search(pattern2));
                matches.addAll(searcher.search(pattern3));
                matches.addAll(searcher.search(pattern4));
                matches.addAll(searcher.search(pattern5));
                matches.addAll(searcher.search(pattern6));
                matches.addAll(searcher.search(pattern7));
                matches.addAll(searcher.search(pattern8));
                matches.addAll(searcher.search(pattern9));
                for (AbstractInsnNode[] match : matches) {
                    FieldInsnNode array = null;
                    FieldInsnNode getter = null;
                    int var = -1;
                    int aload = -1;
                    if (match.length == 7) { //Type 2
                        int op0 = match[0].getOpcode();
                        int op1 = match[1].getOpcode();
                        if (op0 == ILOAD && op1 == ALOAD) {
                            getter = (FieldInsnNode) match[2];
                            var = ((VarInsnNode) match[0]).var;
                            aload = ((VarInsnNode) match[1]).var;
                        } else if (op0 == ILOAD && op1 == LDC) {
                            getter = (FieldInsnNode) match[3];
                            var = ((VarInsnNode) match[0]).var;
                            aload = ((VarInsnNode) match[2]).var;
                        } else if (op0 == ALOAD && op1 == GETFIELD) {
                            getter = (FieldInsnNode) match[1];
                            var = ((VarInsnNode) match[4]).var;
                            aload = ((VarInsnNode) match[0]).var;
                        } else if (op0 == LDC && op1 == ALOAD) {
                            getter = (FieldInsnNode) match[2];
                            var = ((VarInsnNode) match[4]).var;
                            aload = ((VarInsnNode) match[1]).var;
                        }
                        if (getter == null || var == -1 || aload == -1)
                            throw new Error("eek");
                        boolean WX = match(x, getter);
                        boolean WY = match(y, getter);
                        if (!(WX || WY)) continue;
                        InsnList stack = new InsnList();
                        stack.add(new VarInsnNode(ALOAD, aload));
                        stack.add(new VarInsnNode(ILOAD, var));
                        if (WX) stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerX", "I"));
                        if (WY) stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerY", "I"));
                        mn.instructions.insert(match[6], stack); //insert after the statement...
                        System.out.println("Injected Masters<" + (WX ? "X" : "Y") + ">(A=" + aload + ",Var=" + var + ")@" + node.name + "." + mn.name);
                        continue;
                    }

                    //------------------------------------------------------


                    if (match[7].getOpcode() == GETFIELD) {
                        getter = (FieldInsnNode) match[7];
                        var = ((VarInsnNode) match[4]).var;
                        aload = ((VarInsnNode) match[6]).var;
                    }

                    if (match[6].getOpcode() == GETFIELD) {
                        getter = (FieldInsnNode) match[6];
                        var = ((VarInsnNode) match[8]).var;
                        aload = ((VarInsnNode) match[5]).var;
                    }

                    if (match[5].getOpcode() == GETFIELD) {
                        getter = (FieldInsnNode) match[5];
                        var = ((VarInsnNode) match[8]).var;
                        aload = ((VarInsnNode) match[4]).var;
                    }

                    array = (FieldInsnNode) match[0];

                    if (array == null || getter == null || var == -1)
                        throw new Error("eek");

                    boolean X = match(x, array);
                    boolean Y = match(y, array);
                    if (!(X || Y)) continue;

                    boolean WX = match(x, getter);
                    boolean WY = match(y, getter);
                    if (!(WX || WY)) continue;


                    InsnList stack = new InsnList();
                    stack.add(new VarInsnNode(ALOAD, aload));
                    stack.add(new VarInsnNode(ILOAD, var));

                    if (X) stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerX", "I"));
                    if (Y) stack.add(new FieldInsnNode(PUTFIELD, widget.name, "containerY", "I"));

                    mn.instructions.insert(match[10], stack); //insert after the statement...

                    System.out.println("Injected Masters<" + (X ? "X" : "Y") + ">(A=" + aload + ",Var=" + var + ")@" + node.name + "." + mn.name);
                }
            }
        }
    }

    private boolean match(final FieldHook mh, FieldInsnNode fin) {
        return fin != null && fin.owner.equals(mh.clazz) && fin.name.equals(mh.field) && fin.desc.equals(mh.fieldDesc);
    }

    private AbstractInsnNode next(AbstractInsnNode curr, int op) {
        AbstractInsnNode ok = curr;
        while ((ok = ok.getNext()) != null) {
            if (ok.getOpcode() == op)
                return ok;
        }
        return null;
    }

    private AbstractInsnNode prev(AbstractInsnNode curr, int op) {
        AbstractInsnNode ok = curr;
        while ((ok = ok.getPrevious()) != null) {
            if (ok.getOpcode() == op)
                return ok;
        }
        return null;
    }
}
