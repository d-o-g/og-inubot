package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.asm.tree.*;

import java.util.concurrent.atomic.AtomicBoolean;

@VisitorInfo(hooks = {})
public class Varps extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.name.equals("client")) return false;
        AtomicBoolean found = new AtomicBoolean();
        if (cn.getFieldTypeCount() == 0) {
            MethodNode clinit = cn.getMethodByName("<clinit>");
            if (clinit == null) return false;
            TreeBuilder.build(clinit).accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("[I")) {
                        NumberNode sipush = (NumberNode) fmn.layer(NEWARRAY, SIPUSH);
                        if (sipush == null || sipush.number() != 2000)
                            return;
                        found.set(true);
                    }
                }
            });
        }
        return found.get();
    }

    @Override
    public void visit() {

    }
}
