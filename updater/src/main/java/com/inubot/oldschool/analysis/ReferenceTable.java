package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.InvokeHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"buffer", "children", "entry", "unpack"})
public class ReferenceTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_ABSTRACT) > 0 && cn.ownerless() && cn.fieldCount(Object[][].class) == 1 &&
                cn.fieldCount(int[][].class) > 0 && cn.fieldCount(desc("IdentityTable")) > 0;
    }

    @Override
    public void visit() {
        add("buffer", cn.getField(null, "[[Ljava/lang/Object;"));
        add("children", cn.getField(null, "[" + desc("IdentityTable")));
        add("entry", cn.getField(null, desc("IdentityTable")));
        visitIfM(new Unpack(), m -> m.desc.endsWith("[B") && m.desc.startsWith("(II"));
    }

    private class Unpack extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == ARETURN) {
                        MethodMemberNode invoke = n.firstMethod();
                        if (invoke == null || !invoke.desc().startsWith("(II[I") || !invoke.desc().endsWith("[B")) {
                            return;
                        }
                        addHook(new InvokeHook("unpack", invoke.min()));
                    }
                }
            });
        }
    }
}
