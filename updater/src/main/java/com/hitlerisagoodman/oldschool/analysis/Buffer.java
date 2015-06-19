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
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Predicate;

/**
 * @author unsigned
 * @since 06-05-2015
 */
@VisitorInfo(hooks = {"caret", "payload", "writeInt", "readInt", "writeCrc", "matchCrc", "writeShort", "writeLEShort"})
public class Buffer extends GraphVisitor {

    private static final Predicate<MethodNode> CRC_PRED = m -> (m.access & ACC_STATIC) == 0 && (m.desc.endsWith("Z") || m.desc.endsWith("I"));

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 2 && cn.fieldCount("I") == 1 && cn.fieldCount("[B") == 1 && !cn.ownerless();
    }

    @Override
    public void visit() {
        addHook(new FieldHook("caret", cn.getField(null, "I")));
        addHook(new FieldHook("payload", cn.getField(null, "[B")));
        visitLocalIfM(new CrcVisitor(), CRC_PRED); //visit non static method if method satisfies predicate
        if (!hooks.containsKey("writeCrc") || !hooks.containsKey("matchCrc"))
            throw new RuntimeException("FUCKING LOSER YOU SUCK DICK EAT A CONDOM AND DIE NIGGER");
        // Dependant on previous hooks ^
        visitLocalIfM(new IntegerVisitor(), m -> m.key().equals(((InvokeHook) hooks.get("writeCrc")).key())
                || m.key().equals(((InvokeHook) hooks.get("matchCrc")).key()));
    }

    private class CrcVisitor extends BlockVisitor {

        private int profiled = 0;

        @Override
        public boolean validate() {
            return profiled < 2;
        }

        @Override
        public void visit(Block block) {
            boolean match = block.owner.desc.endsWith("Z");
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() != IUSHR)
                        return;
                    NumberNode eight = an.firstNumber();
                    if (eight == null || eight.number() != 8)
                        return;
                    addHook(new InvokeHook(match ? "matchCrc" : "writeCrc", eight.method()));
                    profiled++;
                }
            });
        }
    }

    private class IntegerVisitor extends BlockVisitor {

        private int hooked = 0;

        @Override
        public boolean validate() {
            return hooked < 2;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().endsWith("V") && !hooks.containsKey("writeInt") && mmn.owner().equals(cn.name)) {
                        addHook(new InvokeHook("writeInt", mmn.min()));
                        hooked++;
                    } else if (mmn.desc().endsWith("I") && !hooks.containsKey("readInt") && mmn.owner().equals(cn.name)) {
                        addHook(new InvokeHook("readInt", mmn.min()));
                        hooked++;
                    }
                }
            });
        }
    }
}
