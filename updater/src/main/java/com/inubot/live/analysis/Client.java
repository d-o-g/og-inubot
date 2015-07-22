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
import org.objectweb.asm.commons.cfg.query.*;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.*;

/**
 * @author Dogerina
 * @since 28-06-2015
 */
@VisitorInfo(hooks = {"interfaceBounds", "mapOffset", "mapAngle", "mapScale", "mapState", "menuX", "menuY",
        "menuWidth", "menuHeight", "guidanceArrows", "processAction"})
public class Client extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.name.equalsIgnoreCase("client"); //in case we run on a refactored pack
    }

    @Override
    public void visit() {
        for (ClassNode cn : updater.classnodes.values()) {
            for (FieldNode fn : cn.fields) {
                if ((fn.access & ACC_STATIC) == 0)
                    continue;
                if (fn.desc.equals("[Ljava/awt/Rectangle;")) {
                    addHook(new FieldHook("interfaceBounds", fn));
                } else if (fn.desc.equals("[" + desc("GuidanceArrow"))) {
                    addHook(new FieldHook("guidanceArrows", fn));
                }
            }
            for (MethodNode mn : cn.methods) {
                if ((mn.access & ACC_STATIC) == 0)
                    continue;
                if (mn.desc.startsWith("(" + desc("MenuItem") + "IIZ")) {
                    addHook(new InvokeHook("processAction", mn));
                }
            }
        }
        visitAll(new AngularMap(), new MapScale(), new MapState());
        visitIfM(new MenuBounds(), m -> m.desc.startsWith("(" + desc("RenderConfiguration")));
    }

    private class MenuBounds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new InsnQuery(IFNE)) > 0) {
                block.tree().accept(new NodeVisitor() {
                    public void visitMethod(MethodMemberNode mmn) {
                        if (mmn.desc().startsWith("(" + desc("RenderConfiguration") + "IIIIII")) {
                            AbstractNode xmul = mmn.find(IMUL, 0);
                            if (xmul == null) return;
                            FieldMemberNode x = xmul.firstField();
                            if (x == null || x.opcode() != GETSTATIC) return;
                            AbstractNode ymul = mmn.find(IMUL, 1);
                            if (ymul == null) return;
                            FieldMemberNode y = ymul.firstField();
                            if (y == null || y.opcode() != GETSTATIC) return;
                            AbstractNode wmul = mmn.find(IMUL, 2);
                            if (wmul == null) return;
                            FieldMemberNode w = wmul.firstField();
                            if (w == null || w.opcode() != GETSTATIC) return;
                            AbstractNode hmul = mmn.find(IMUL, 3);
                            if (hmul == null) return;
                            FieldMemberNode h = hmul.firstField();
                            if (h == null || h.opcode() != GETSTATIC) return;
                            addHook(new FieldHook("menuX", x.fin()));
                            addHook(new FieldHook("menuY", y.fin()));
                            addHook(new FieldHook("menuWidth", w.fin()));
                            addHook(new FieldHook("menuHeight", h.fin()));
                            lock.set(true);
                        }
                    }
                });
            }
        }
    }

    private class MapState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new MemberQuery(GETFIELD, clazz("Widget"), "I")) != 2 ||
                    block.count(new InsnQuery(ISUB)) != 2 || block.count(new InsnQuery(IDIV)) != 2)
                return;
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    FieldMemberNode state = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                    if (state != null) {
                        addHook(new FieldHook("mapState", state.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class AngularMap extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(SIPUSH, 0x3FFF)) == 0 || block.count(new MemberQuery(GETSTATIC)) != 2)
                return;
            block.tree().accept(new NodeVisitor() {
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IAND) {
                        FieldMemberNode offset = (FieldMemberNode) an.layer(IADD, IMUL, GETSTATIC);
                        FieldMemberNode angle = (FieldMemberNode) an.layer(IADD, F2I, GETSTATIC);
                        if (angle == null || offset == null)
                            return;
                        addHook(new FieldHook("mapOffset", offset.fin()));
                        addHook(new FieldHook("mapAngle", angle.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class MapScale extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new InsnQuery(ISHR)) != 2 || block.count(new NumberQuery(SIPUSH, 0x0100)) != 2)
                return;
            block.tree().accept(new NodeVisitor() {
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == ISHR) {
                        FieldMemberNode scale = (FieldMemberNode) an.layer(IMUL, IADD, IMUL, GETSTATIC);
                        if (scale != null) {
                            addHook(new FieldHook("mapScale", scale.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}
