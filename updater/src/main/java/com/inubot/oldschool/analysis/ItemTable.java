/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import com.inubot.modscript.hook.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author unsigned
 * @since 03-05-2015
 */
@VisitorInfo(hooks = {"ids", "stackSizes"})
public class ItemTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 2 && cn.fieldCount("[I") == 2 && cn.superName.equals(clazz("Node"));
    }

    @Override
    public void visit() {
        visitIfM(new Hooks(), m -> Modifier.isStatic(m.access));
    }

    private class Hooks extends BlockVisitor {

        private final List<Setter> vars = new ArrayList<>();

        @Override
        public boolean validate() {
            return vars.size() < 2 || !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.owner().equals(clazz("NodeTable")) && mmn.desc().startsWith("(J")
                            && mmn.hasChild(I2L) && mmn.hasChild(GETSTATIC) && mmn.nextType() != null
                            && mmn.nextType().type().equals(cn.name)) {
                        updater.visitor("Client").addHook(new FieldHook("itemTables", mmn.firstField().fin()));
                        lock.set(true);
                    }
                }

                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (vars.size() < 2 && fmn.owner().equals(cn.name) && fmn.desc().equals("[I") && fmn.putting()) {
                        VariableNode value = (VariableNode) fmn.last(ALOAD);
                        if (value == null)
                            return;
                        Setter set = new Setter();
                        set.ref = fmn.fin();
                        set.var = value.var();
                        vars.add(set);
                    }
                }
            });
        }

        @Override
        public void visitEnd() {
            if (vars.size() < 2)
                return;
            addHook(new FieldHook("ids", vars.get(0).ref));
            addHook(new FieldHook("stackSizes", vars.get(1).ref));
        }
    }

    private class Setter {
        private int var;
        private FieldInsnNode ref;
    }
}
