/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author unsigned
 * @since 22-04-2015
 */
@VisitorInfo(hooks = {"flags", "insetX", "insetY", "width", "height"})
public class CollisionMap extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getFieldTypeCount() == 2 && cn.fieldCount("[[I") == 1 && cn.fieldCount("I") == 4;
    }

    @Override
    public void visit() {
        add("flags", cn.getField(null, "[[I"));
        visitLocalIfM(new Size(), m -> m.name.equals("<init>"));
        visitLocalIfM(new Insets(), m -> m.desc.startsWith("(IIIIZ") && m.desc.endsWith("V"));
    }

    private class Insets extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == ISUB && an.hasParent() && an.parent().opcode() == ISTORE) {
                        VariableNode store = (VariableNode) an.parent();
                        FieldMemberNode storedest = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                        if (storedest != null && an.hasChild(ILOAD)) {
                            int var = store.var();
                            if ((var == 1 || var == 2) && var == ((VariableNode) an.first(ILOAD)).var()) {
                                addHook(new FieldHook(var == 1 ? "insetX" : "insetY", storedest.fin()));;
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
            return added < 6;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                        VariableNode vn = (VariableNode) fmn.layer(IMUL, ILOAD);
                        if (vn == null) {
                            vn = (VariableNode) fmn.layer(IMUL, ISUB, IADD, ILOAD);
                            if (vn != null) vn = vn.nextVariable();
                        }
                        if (vn != null) {
                            String name = null;
                            if (vn.var() == 1) {
                                name = "width";
                            } else if (vn.var() == 2) {
                                name = "height";
                            }
                            if (name == null) {
                                return;
                            }
                            hooks.put(name, new FieldHook(name, fmn.fin()));
                            added++;
                        }
                    }
                }
            });
        }
    }
}
