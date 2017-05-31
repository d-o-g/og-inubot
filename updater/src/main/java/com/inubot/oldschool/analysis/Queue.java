package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"head"})
public class Queue extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fields.size() == 1 && cn.fieldCount(desc("DoublyLinkedNode")) == 1 &&
                !cn.interfaces.contains("java/lang/Iterable");
    }

    @Override
    public void visit() {
        add("head", cn.getField(null, desc("DoublyLinkedNode")), literalDesc("DoublyLinkedNode"));
    }
}