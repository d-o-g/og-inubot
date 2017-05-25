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
import com.inubot.modscript.hook.InvokeHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

/**
 * @author unsigned
 * @since 29-04-2015
 */
@VisitorInfo(hooks = {"left", "right", "varpIndex"})
public class VarpBit extends GraphVisitor {

    private final List<VarAssignment> vars = new ArrayList<>();

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("CacheNode")) && cn.fieldCount() == 3 && cn.fieldCount("I") == 3;
    }

    @Override
    public void visit() {
        visitIfM(new VarpBitVisitor(), m -> (m.access & ACC_STATIC) != 0);
        vars.sort(new Comparator<VarAssignment>() {
            @Override
            public int compare(VarAssignment o1, VarAssignment o2) {
                return Integer.compare(o1.istore.var(), o2.istore.var());
            }
        });
        for (final VarAssignment var : vars) {
            if (hooks.get("varpIndex") == null) {
                addHook(new FieldHook("varpIndex", var.getfield.fin()));
            } else if (hooks.get("left") == null) {
                addHook(new FieldHook("left", var.getfield.fin()));
            } else if (hooks.get("right") == null) {
                addHook(new FieldHook("right", var.getfield.fin()));
            }
        }
        visitIfM(new Backup(), m -> (m.access & ACC_STATIC) != 0);
    }

    private class Backup extends BlockVisitor {

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitType(TypeNode tn) {
                    if (tn.opcode() != CHECKCAST || !tn.type().equals(cn.name))
                        return;
                    updater.visitor("Client").addHook(new InvokeHook("getVarpBit", tn.method()));
                }
            });
        }
    }

    private class VarAssignment {
        private VariableNode istore;
        private FieldMemberNode getfield;
    }

    private class VarpBitVisitor extends BlockVisitor {

        private boolean canAdd(VarAssignment var) {
            for (VarAssignment var0 : vars) {
                if (var0.getfield.key().equals(var.getfield.key())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitVariable(VariableNode sn) {
                    if (sn.opcode() != ISTORE)
                        return;
                    FieldMemberNode fmn = (FieldMemberNode) sn.layer(IMUL, GETFIELD);
                    if (fmn == null || !fmn.owner().equals(cn.name))
                        return;
                    VarAssignment var = new VarAssignment();
                    var.istore = sn;
                    var.getfield = fmn;
                    if (!canAdd(var))
                        return;
                    vars.add(var);
                    sn.tree().accept(new NodeVisitor() {
                        @Override
                        public void visitVariable(VariableNode sn) {
                            if (sn.opcode() != ASTORE)
                                return;
                            MethodMemberNode invokestadik = sn.firstMethod();
                            if (invokestadik != null && invokestadik.desc().contains("I")) {
                                updater.visitor("Client").addHook(new InvokeHook("getVarpBit", invokestadik.min()));
                            }
                        }
                    });
                }
            });
        }
    }
}
