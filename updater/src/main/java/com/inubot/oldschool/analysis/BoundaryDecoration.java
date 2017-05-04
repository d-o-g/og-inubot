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
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author unsigned
 * @since 26-04-2015
 */
@VisitorInfo(hooks = {"plane", "x", "y", "id", "renderable"})
public class BoundaryDecoration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getFieldTypeCount() == 2 && cn.fieldCount("I") == 9 && cn.fieldCount(desc("Renderable")) == 2;
    }

    @Override
    public void visit() {
        visit("Region", new Hooks());
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 5;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(cn.name)) {
                        if (fmn.desc().equals("I")) {
                            VariableNode vn = (VariableNode) fmn.layer(IMUL, ILOAD);
                            if (vn == null) {
                                vn = (VariableNode) fmn.layer(IADD, IMUL, ILOAD);
                            }
                            if (vn != null) {
                                String name = null;
                                if (vn.var() == 2) {
                                    name = "x";
                                } else if (vn.var() == 3) {
                                    name = "y";
                                } else if (vn.var() == 4) {
                                    name = "plane";
                                } else if (vn.var() == 11) {
                                    name = "id";
                                }
                                if (name == null) return;
                                hooks.put(name, new FieldHook(name, fmn.fin()));
                                added++;
                            }
                        } else if (fmn.desc().equals(desc("Renderable"))) {
                            VariableNode vn = fmn.firstVariable();
                            if (vn != null) {
                                vn = vn.nextVariable();
                            }
                            if (vn != null) {
                                String name = null;
                                if (vn.var() == 5) {
                                    name = "renderable";
                                }
                                if (name == null) {
                                    return;
                                }
                                addHook(new FieldHook(name, fmn.fin()));
                                hooks.put(name, new FieldHook(name, fmn.fin()));
                                added++;
                            }
                        }
                    }
                }
            });
        }
    }
}
