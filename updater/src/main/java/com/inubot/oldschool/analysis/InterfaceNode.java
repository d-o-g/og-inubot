package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import com.inubot.modscript.hook.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"id"})
public class InterfaceNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("I") == 2 && cn.fieldCount("Z") == 1;
    }

    @Override
    public void visit() {
        visitAll(new Id());
    }

    private class Id extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(ALOAD) == 2 && block.count(IMUL) == 1 && block.count(GETFIELD) == 1) {
                block.tree().accept(new NodeVisitor() {
                    public void visitJump(JumpNode jn) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                        if (fmn == null || !fmn.owner().equals(cn.name) || fmn.children() != 1 || fmn.first(ALOAD) == null)
                            return;
                        addHook(new FieldHook("id", fmn.fin()));
                        lock.set(true);
                    }
                });
            }
        }
    }
}