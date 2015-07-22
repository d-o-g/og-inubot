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
@VisitorInfo(hooks = {"vendorId", "name", "id", "description", "version"})
public class RenderMode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(long.class) == 1 && cn.fieldCount(int.class) == 2 && cn.fieldCount(String.class) == 2
                && cn.getMethod("<init>", "(ILjava/lang/String;ILjava/lang/String;JZ)V") != null;
    }

    @Override
    public void visit() {
        visitLocalIfM(new Hooks(), m -> m.name.equals("<init>") && m.desc.equals("(ILjava/lang/String;ILjava/lang/String;JZ)V"));
    }

    //vendorId, name, id, desc, version
    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 5;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitVariable(VariableNode vn) {
                    if ((vn.opcode() == ILOAD || vn.opcode() == ALOAD || vn.opcode() == LLOAD)
                            && (vn.parent() != null && vn.parent().opcode() == PUTFIELD
                            || vn.parent().parent() != null && vn.parent().parent().opcode() == PUTFIELD)) {
                        FieldMemberNode fmn = (FieldMemberNode) (vn.parent().opcode() == PUTFIELD ? vn.parent() : vn.parent().parent());
                        if (vn.var() == 1) {
                            addHook(new FieldHook("vendorId", fmn.fin()));
                            added++;
                        } else if (vn.var() == 2) {
                            addHook(new FieldHook("name", fmn.fin()));
                            added++;
                        } else if (vn.var() == 3) {
                            addHook(new FieldHook("id", fmn.fin()));
                            added++;
                        } else if (vn.var() == 4) {
                            addHook(new FieldHook("description", fmn.fin()));
                            added++;
                        } else if (vn.var() == 5) {
                            addHook(new FieldHook("version", fmn.fin()));
                            added++;
                        }
                    }
                }
            });
        }
    }
}
