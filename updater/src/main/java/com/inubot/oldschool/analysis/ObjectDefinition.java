package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.modscript.hook.InvokeHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;

@VisitorInfo(hooks = {"name", "actions", "id", "transformIds", "varpIndex", "varpBitIndex", "transform", "mapFunction", "colors", "newColors"})
public class ObjectDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 7 && cn.fieldCount("[S") == 4 && cn.fieldCount("[I") == 4;
    }

    @Override
    public void visit() {
        add("name", cn.getField(null, "Ljava/lang/String;"), "Ljava/lang/String;");
        add("actions", cn.getField(null, "[Ljava/lang/String;"), "[Ljava/lang/String;");
        visit(new Id());
        visitLocalIfM(new TransformIds(), m -> !Modifier.isStatic(m.access) && m.desc.endsWith("L" + cn.name + ";"));
        visit(new VarpbitIndex());
        visit(new VarpIndex());
        visitAll(new MapFunction());
        visitAll(new Colors());
        for (MethodNode mn : cn.methods) {
            if (!Modifier.isStatic(mn.access) && mn.desc.endsWith("L" + cn.name + ";")) {
                addHook(new InvokeHook("transform", mn));
            }
        }
    }

    private class Colors extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() != INVOKEVIRTUAL)
                        return;
                    ClassNode refowner = updater.classnodes.get(mmn.owner());
                    if (refowner == null)
                        return;
                    MethodNode mn = refowner.getMethod(mmn.name(), mmn.desc());
                    if (mn != null && !contains(mn.instructions, IFNONNULL)) {
                        mmn.tree().accept(new NodeVisitor() {
                            @Override
                            public void visitField(FieldMemberNode fmn) {
                                if (!fmn.desc().equals("[S") || !fmn.owner().equals(cn.name))
                                    return;
                                if (!hooks.containsKey("colors")) {
                                    addHook(new FieldHook("colors", fmn.fin()));
                                } else if (!hooks.containsKey("newColors")) {
                                    addHook(new FieldHook("newColors", fmn.fin()));
                                }
                            }
                        });
                    }
                }
            });
        }


        private boolean contains(InsnList iList, int op) {
            for (AbstractInsnNode ain : iList.toArray()) {
                if (ain.opcode() == op) {
                    return true;
                }
            }
            return false;
        }
    }

    private class MapFunction extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == GETFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                        MethodMemberNode method = fmn.firstMethod();
                        if (method != null && method.desc().endsWith("L" + cn.name + ";")) {
                            VariableNode vn = method.firstVariable();
                            if (vn != null) {
                                hooks.put("mapFunction", new FieldHook("mapFunction", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Id extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == GETFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                        if (fmn.preLayer(IMUL, ISHL, IADD, IADD, I2L) != null) {
                            hooks.put("id", new FieldHook("id", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class TransformIds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visit(AbstractNode n) {
                    if (n.opcode() == IALOAD) {
                        FieldMemberNode fmn = (FieldMemberNode) n.layer(ISUB, ARRAYLENGTH, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name)) {
                            addHook(new FieldHook("transformIds", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class VarpbitIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ISTORE && vn.var() == 2) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKESTATIC, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name) && fmn.first(ALOAD) != null) {
                            addHook(new FieldHook("varpBitIndex", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class VarpIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ISTORE && vn.var() == 2) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.layer(IALOAD, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name) && fmn.first(ALOAD) != null) {
                            addHook(new FieldHook("varpIndex", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}
