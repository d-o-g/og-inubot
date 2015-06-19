/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import com.hitlerisagoodman.modscript.hook.FieldHook;
import com.hitlerisagoodman.modscript.hook.InvokeHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author unsigned
 * @since 18-05-2015
 */
@VisitorInfo(hooks = {"width", "height", "pixels"})
public class Sprite extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 7 && cn.fieldCount("I") == 6 && cn.fieldCount("[I") == 1 && !cn.ownerless() && (cn.access & ACC_FINAL) != 0;
    }

    @Override
    public void visit() {
        for (ClassNode cn : updater.classnodes.values()) {
            cn.methods.stream().filter(mn -> mn.desc.startsWith("(IIIIIZ") && mn.desc.endsWith(")L" + this.cn.name + ";"))
                    .forEach(mn -> updater.visitor("Client").addHook(new InvokeHook("loadItemSprite", mn)));
        }
        visitAll(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitMethod(MethodMemberNode mmn) {
                        if (mmn.opcode() == INVOKESTATIC && mmn.desc().startsWith("([III")) {
                            FieldMemberNode pixels = mmn.firstField();
                            if (pixels == null || !pixels.owner().equals(cn.name))
                                return;
                            FieldMemberNode width = pixels.nextField();
                            if (width == null || !width.owner().equals(cn.name))
                                return;
                            FieldMemberNode height = width.nextField();
                            if (height == null || !height.owner().equals(cn.name))
                                return;
                            addHook(new FieldHook("pixels", pixels.fin()));
                            addHook(new FieldHook("height", height.fin()));
                            addHook(new FieldHook("width", width.fin()));
                            lock.set(true);
                        }
                    }
                });
            }
        });
    }
}
