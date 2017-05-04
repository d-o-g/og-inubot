package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by Inspiron on 14/07/2016.
 */
@VisitorInfo(hooks = {"cycle", "health"})
public class Hitbar extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount() == 4 && cn.fieldCount(int.class) == 4;
    }

    @Override
    public void visit() {
        visitLocalIfM(new Hooks(), m -> m.name.equals("<init>"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 6;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                        VariableNode vn = (VariableNode) fmn.layer(IMUL, ILOAD);
                        if (vn == null) {
                            vn = (VariableNode) fmn.layer(IMUL, ISUB, IADD, ILOAD);
                            if (vn != null) vn = vn.nextVariable();
                        }
                        if (vn != null) {
                            String name = null;
                            if (vn.var() == 1) {
                                name = "cycle";
                            } else if (vn.var() == 2) {
                                name = "health";
                            }
                            if (name == null) {
                                return;
                            }
                            hooks.put(name, new FieldHook(name, fmn.fin()));
                            added++;
                        }
                    }
                }
            });
        }
    }
}