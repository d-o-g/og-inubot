/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.modscript.hook.FieldHook;
import com.hitlerisagoodman.modscript.hook.InvokeHook;
import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author unsigned
 * @since 09-06-2015
 */
@VisitorInfo(hooks = {"drawString"})
public class Font extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return !cn.ownerless() && cn.getMethod("<init>", "([B[I[I[I[I[I[[B)V") != null && cn.fieldCount() == 0;
    }

    @Override
    public void visit() {
        visitAll(new DrawString());
    }

    private class DrawString extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitConstant(ConstantNode c) {
                    if (c.cst().equals("Fps:")) {
                        c.tree().accept(new NodeVisitor() {
                            @Override
                            public void visitMethod(MethodMemberNode mmn) {
                                if (!mmn.owner().equals(cn.name))
                                    return;
                                for (MethodNode mn : updater.classnodes.get(cn.superName).methods) {
                                    if (mn.name.equals(mmn.name()) && mn.desc.equals(mmn.desc())) {
                                        updater.graphs().get(mn.owner).get(mn).forEach(b -> b.tree().accept(new NodeVisitor() {
                                            @Override
                                            public void visitOperation(ArithmeticNode an) {
                                                if (an.opcode() == ISUB && an.hasParent() && an.parent().opcode() == INVOKEVIRTUAL)
                                                    addHook(new InvokeHook("drawString", ((MethodMemberNode) an.parent()).min()));
                                            }
                                        }));
                                    }
                                }
                            }
                        });
                        FieldMemberNode fmn = (FieldMemberNode) c.previous().layer(DUP, GETSTATIC);
                        if (fmn != null)
                            updater.visitor("Client").addHook(new FieldHook("font_p12full", fmn.fin()));
                    }
                }
            });
        }
    }
}
