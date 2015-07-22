/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.modscript.hook.InvokeHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.InsnQuery;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Hashtable;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"modeId", "hashtable", "getMode"})
public class RenderConfiguration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && (cn.access & ACC_ABSTRACT) != 0 && cn.fieldCount("I") >= 2 && cn.fieldCount(Hashtable.class) == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("hashtable", cn.getField(null, "Ljava/util/Hashtable;")));
        visitIfM(new Mode(), m -> m.desc.startsWith("(" + desc()));
        String desc = desc("RenderMode");
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.endsWith(desc)) {
                addHook(new InvokeHook("getMode", mn));
            }
        }
    }

    private class Mode extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            if (block.count(new InsnQuery(IOR)) == 2) {
                block.tree().accept(new NodeVisitor() {
                    public void visitVariable(VariableNode vn) {
                        if (vn.opcode() == LSTORE) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(I2L, IOR, ISHL, IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                addHook(new FieldHook("modeId", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }
}
