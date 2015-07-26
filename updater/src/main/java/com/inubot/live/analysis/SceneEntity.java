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
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"startX", "startY", "endX", "endY"})
public class SceneEntity extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(short.class) == 4 && cn.superName.equals(clazz("SceneNode"));
    }

    @Override
    public void visit() {
        visitLocalIfM(new BlockVisitor() {

            private int added = 0;

            @Override
            public boolean validate() {
                return added < 4;
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    public void visitVariable(VariableNode vn) {
                        if (!vn.hasParent()) {
                            return;
                        }
                        AbstractNode ok = vn.opcode() == ALOAD ? vn.parent() : vn.parent().parent();
                        if (ok == null || !(ok instanceof FieldMemberNode)) {
                            return;
                        }
                        FieldMemberNode fmn = (FieldMemberNode) ok;
                        switch (vn.var()) {
                            case 7:
                                addHook(new FieldHook("startX", fmn.fin()));
                                added++;
                                break;
                            case 8:
                                addHook(new FieldHook("startY", fmn.fin()));
                                added++;
                                break;
                            case 9:
                                addHook(new FieldHook("endX", fmn.fin()));
                                added++;
                                break;
                            case 10:
                                addHook(new FieldHook("endY", fmn.fin()));
                                added++;
                                break;
                        }
                    }
                });
            }
        }, m -> m.name.equals("<init>"));
    }
}
