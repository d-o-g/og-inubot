/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"buckets", "size"})
public class NodeTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.interfaces != null && cn.interfaces.contains("java/lang/Iterable") && cn.fieldCount(desc("Node")) == 2
                && cn.fieldCount(long.class) == 1 && cn.fieldCount(int.class) == 2;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("buckets", cn.getField(null, "[" + desc("Node"))));
        visitLocalIfM(new ConstructorHooks(), m -> m.desc.equals("(I)V") && m.name.equals("<init>"));
    }

    private class ConstructorHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ILOAD && vn.parent() != null && vn.parent().opcode() == PUTFIELD) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.parent();
                        if (vn.var() == 1) {
                            addHook(new FieldHook("size", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}
