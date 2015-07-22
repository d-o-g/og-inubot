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
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"hash", "next", "previous"})
public class Node extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount("L" + cn.name + ";") == 2 && cn.fieldCount("J") == 1 && cn.fieldCount() == 3;
    }

    @Override
    public void visit() {
        add("hash", cn.getField(null, "J"));
        visit(new Hooks());
    }

    private class Hooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IFNONNULL) {
                        FieldMemberNode fmn = jn.firstField();
                        if (fmn != null && fmn.desc().equals("L" + cn.name + ";")) {
                            addHook(new FieldHook("previous", fmn.fin()));
                            for (FieldNode fn : cn.fields) {
                                if (fn.desc.equals("L" + cn.name + ";")) {
                                    if (!fn.name.equals(fmn.name())) {
                                        addHook(new FieldHook("next", fn));
                                        break;
                                    }
                                }
                            }
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}
