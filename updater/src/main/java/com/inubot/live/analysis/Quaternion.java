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
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.tree.ClassNode;

import java.util.function.Consumer;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"h", "i", "j", "k"})
public class Quaternion extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 1 && cn.fieldCount("F") == 4 &&
                cn.constructors().contains("(L" + cn.name + ";)V");
    }

    @Override
    public void visit() {
        visitLocalIfM(new Hooks(), m -> m.name.equals("<init>") && m.desc.equals("(FFFF)V"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 4;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.owner().equals(cn.name)) {
                        updater.graphs().get(cn).get(cn.getMethod(mmn.name(), mmn.desc())).forEach(b -> {
                            b.tree().accept(new NodeVisitor() {
                                @Override
                                public void visitField(FieldMemberNode fmn) {
                                    if (!fmn.desc().equals("F")) {
                                        return;
                                    }
                                    if (hooks.get("i") == null) {
                                        addHook(new FieldHook("i", fmn.fin()));
                                        added++;
                                    } else if (hooks.get("j") == null) {
                                        addHook(new FieldHook("j", fmn.fin()));
                                        added++;
                                    } else if (hooks.get("k") == null) {
                                        addHook(new FieldHook("k", fmn.fin()));
                                        added++;
                                    } else if (hooks.get("h") == null) {
                                        addHook(new FieldHook("h", fmn.fin()));
                                        added++;
                                    }
                                }
                            });
                        });
                    }
                }
            });
        }
    }
}
