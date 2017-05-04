package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

public class NodeIterable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount() == 2 && cn.fieldCount(desc("Node")) == 2 && cn.interfaces.size() == 1;
    }

    @Override
    public void visit() {

    }
}
