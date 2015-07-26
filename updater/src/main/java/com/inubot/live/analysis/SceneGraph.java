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
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"levels", "levelCount", "width", "height", "tiles"})
public class SceneGraph extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.ownerless() && cf.fieldCount("[[[" + desc("SceneGraphTile")) > 0;
    }

    @Override
    public void visit() {
        visitLocalIfM(new Levels(), m -> m.desc.startsWith("(IIII[[[B"));
        visitLocalIfM(new Size(), m -> m.name.equals("<init>"));
        visitLocalIfM(new Tiles(), m -> m.desc.startsWith("(Z") && m.desc.endsWith("V"));
    }

    private class Tiles extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(cn.name) && getFieldHook("levelCount").field.equals(fmn.name())) {
                        if (fmn.opcode() == PUTFIELD) {
                            FieldMemberNode tiles = (FieldMemberNode) fmn.layer(IMUL, ARRAYLENGTH, GETFIELD);
                            if (tiles != null && tiles.desc().startsWith("[[[L")) {
                                addHook(new FieldHook("tiles", tiles.fin()));
                            }
                        }
                    }
                }
            });
        }
    }

    //int var29 = this.a[this.a.length - 1].l(var25, var26, 224472277) - (1000 << this.g * 1837465099 - 7);
    private class Levels extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == ISHL) {
                        NumberNode nn = an.firstNumber();
                        if (nn != null && nn.number() == 1000) {
                            AbstractNode sub = an.parent();
                            if (sub != null && sub.opcode() == ISUB) {
                                FieldMemberNode fmn = (FieldMemberNode) sub.layer(INVOKEVIRTUAL, AALOAD, ISUB, ARRAYLENGTH, GETFIELD);
                                if (fmn != null && fmn.desc().startsWith("[L")) {
                                    addHook(new FieldHook("levels", fmn.fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class Size extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
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
                    if (!fmn.desc().equals("I")) {
                        return;
                    }
                    switch (vn.var()) {
                        case 3:
                            addHook(new FieldHook("levelCount", fmn.fin()));
                            added++;
                            break;
                        case 4:
                            addHook(new FieldHook("width", fmn.fin()));
                            added++;
                            break;
                        case 5:
                            addHook(new FieldHook("height", fmn.fin()));
                            added++;
                            break;
                    }
                }
            });
        }
    }
}
