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
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"z", "x", "y"})
public class Vector3f extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.ownerless() && cf.fieldCount() == 3 && cf.fieldCount("F") == 3
                && cf.fieldTypeCountIn(nodeFor(CoordinateSpace.class)) == 1;
    }

    @Override
    public void visit() {
        visitLocalIfM(new Hooks(), m -> m.name.equals("<init>") && m.desc.equals("(FFF)V"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() != FLOAD || vn.parent() == null || vn.parent().opcode() != PUTFIELD)
                        return;
                    FieldMemberNode fmn = (FieldMemberNode) vn.parent();
                    switch (vn.var()) {
                        case 1:
                            addHook(new FieldHook("x", fmn.fin()));
                            break;
                        case 2:
                            addHook(new FieldHook("z", fmn.fin()));
                            break;
                        case 3:
                            addHook(new FieldHook("y", fmn.fin()));
                            break;
                    }
                }
            });
        }
    }
}
