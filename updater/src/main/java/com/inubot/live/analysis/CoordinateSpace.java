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
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"rotation", "translation"})
public class CoordinateSpace extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.fieldCount() == 2 && cf.getAbnormalFieldCount() == 2 && cf.ownerless()
                && cf.constructors().contains("(L" + cf.name + ";)V");
    }

    @Override
    public void visit() {
        visitIfM(new Hooks(), m -> m.name.equals("toString"));
    }

    private class Hooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (!hooks.containsKey("rotation")) {
                        addHook(new FieldHook("rotation", fmn.fin()));
                    } else if (!hooks.containsKey("translation")) {
                        addHook(new FieldHook("translation", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }
}
