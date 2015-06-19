package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import com.hitlerisagoodman.modscript.hook.FieldHook;
import com.hitlerisagoodman.util.ArrayIterator;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"name", "id", "actions", "groundActions"})
public class ItemDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("CacheNode")) && cn.getFieldTypeCount() == 6 &&
                cn.fieldCount("[Ljava/lang/String;") == 2;
    }

    @Override
    public void visit() {
        add("name", cn.getField(null, "Ljava/lang/String;"), "Ljava/lang/String;");
        visitAll(new Id());
        visit(new Actions());
    }

    private class Actions extends BlockVisitor {

        private final ArrayIterator<String> itr = new ArrayIterator<>("groundActions", "actions");

        @Override
        public boolean validate() {
            return itr.hasNext();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("[Ljava/lang/String;")) {
                        if (fmn.layer(AASTORE, DUP, AASTORE) == null)
                            return;
                        addHook(new FieldHook(itr.next(), fmn.fin()));
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
                public void visitMethod(MethodMemberNode rn) {
                    if (rn.opcode() == INVOKESTATIC && rn.desc().startsWith("(Ljava/lang/String;Ljava/lang/String;II")) {
                        NumberNode nn = rn.firstNumber();
                        if (nn != null && nn.number() == 1005) {
                            FieldMemberNode fmn = (FieldMemberNode) rn.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                hooks.put("id", new FieldHook("id", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}
