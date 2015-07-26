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
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"m1x1", "m1x2", "m1x3", "m2x1", "m2x2", "m2x3", "m3x1", "m3x2", "m3x3", "m4x1", "m4x2", "m4x3"})
public class Matrix4x3 extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 1 && cn.fieldCount(float.class) == 12;
    }

    @Override
    public void visit() {
        visitLocalIfM(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
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
                                addHook(new FieldHook("m1x1", fmn.fin()));
                                break;
                            case 2:
                                addHook(new FieldHook("m1x2", fmn.fin()));
                                break;
                            case 3:
                                addHook(new FieldHook("m1x3", fmn.fin()));
                                break;
                            case 4:
                                addHook(new FieldHook("m2x1", fmn.fin()));
                                break;
                            case 5:
                                addHook(new FieldHook("m2x2", fmn.fin()));
                                break;
                            case 6:
                                addHook(new FieldHook("m2x3", fmn.fin()));
                                break;
                            case 7:
                                addHook(new FieldHook("m3x1", fmn.fin()));
                                break;
                            case 8:
                                addHook(new FieldHook("m3x2", fmn.fin()));
                                break;
                            case 9:
                                addHook(new FieldHook("m3x3", fmn.fin()));
                                break;
                        }
                    }
                });
            }
        }, m -> m.desc.endsWith("V") && m.desc.startsWith("(FFFFFFFFF"));
        visitLocalIfM(new BlockVisitor() {

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
                                addHook(new FieldHook("m4x1", fmn.fin()));
                                break;
                            case 2:
                                addHook(new FieldHook("m4x2", fmn.fin()));
                                break;
                            case 3:
                                addHook(new FieldHook("m4x3", fmn.fin()));
                                break;
                        }
                    }
                });
            }
        }, m -> m.desc.startsWith("(FFF") && Type.getArgumentTypes(m.desc).length < 5 && m.desc.endsWith("V"));
    }
}
