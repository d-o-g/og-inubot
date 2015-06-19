package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import com.hitlerisagoodman.modscript.hook.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.NumberQuery;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

@VisitorInfo(hooks = {"x", "y", "health", "maxHealth", "interactingIndex", "animation", "healthBarCycle"})
public class Character extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Renderable")) && cn.fieldCount("[I") == 5 && cn.fieldCount("Z") >= 1 &&
                cn.fieldCount("Ljava/lang/String;") == 1;
    }

    @Override
    public void visit() {
        visitAll(new PositionHooks());
        visitAll(new HealthHooks());
        visitAll(new InteractingIndex());
        visitAll(new Animation());
        visitAll(new CombatCycle());
    }

    private class CombatCycle extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPLE) {
                        FieldMemberNode cycle = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                        if (cycle == null || !cycle.owner().equals(clazz("Character")) || !cycle.desc().equals("I"))
                            return;
                        MethodNode init = updater.visitor("Character").cn.getMethodByName("<init>");
                        if (cycle.referenced(init)) {
                            ArithmeticNode an = cycle.parent().nextOperation();
                            if (an == null || an.opcode() != IMUL)
                                return;
                            ReferenceNode rn = an.firstField();
                            if (rn != null && rn.owner().equals("client") && rn.desc().equals("I")) {
                                addHook(new FieldHook("healthBarCycle", cycle.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class PositionHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(SIPUSH, 13184)) <= 0)
                return;
            block.tree().accept(new NodeVisitor() {
                public void visitJump(JumpNode jn) {
                    String name = null;
                    if (jn.opcode() == IF_ICMPGE) {
                        name = "x";
                    } else if (jn.opcode() == IF_ICMPLT) {
                        name = "y";
                    }
                    if (name == null || hooks.containsKey(name))
                        return;
                    FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                    if (fmn == null)
                        return;
                    addHook(new FieldHook(name, fmn.fin()));
                    added++;
                }
            });
        }
    }

    private class HealthHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(IDIV) <= 0 || block.count(GETFIELD) <= 1)
                return;
            block.tree().accept(new NodeVisitor(this) {
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IDIV && an.preLayer(ISTORE) != null && an.layer(IMUL) != null) {
                        for (final AbstractNode imul : an) {
                            FieldMemberNode fmn = imul.firstField();
                            if (fmn != null && fmn.owner().equals(cn.name) && getHookKey("maxHealth") == null) {
                                addHook(new FieldHook("maxHealth", fmn.fin()));
                            }
                            for (final AbstractNode child : imul) {
                                fmn = child.firstField();
                                if (fmn != null && fmn.owner().equals(cn.name) && getHookKey("health") == null) {
                                    addHook(new FieldHook("health", fmn.fin()));
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class InteractingIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visit(AbstractNode n) {
                    if (n.opcode() == AALOAD) {
                        FieldMemberNode fmn = n.firstField();
                        if (fmn != null && fmn.desc().equals("[" + desc("Npc"))) {
                            fmn = (FieldMemberNode) n.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                                hooks.put("interactingIndex", new FieldHook("interactingIndex", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Animation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(clazz("AnimationSequence")) && fmn.desc().equals("I")) {
                        NumberNode nn = (NumberNode) fmn.layer(INVOKESTATIC, IMUL, LDC);
                        fmn = (FieldMemberNode) fmn.layer(INVOKESTATIC, IMUL, GETFIELD);
                        if (fmn != null && nn != null) {
                            FieldHook fh = new FieldHook("animation", fmn.fin());
                            fh.multiplier = nn.number();
                            addHook(fh);
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}