package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"head"})
public class Queue extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fields.size() == 1 && cn.fieldCount(desc("CacheNode")) == 1 &&
                !cn.interfaces.contains("java/lang/Iterable");
    }

    @Override
    public void visit() {
        add("head", cn.getField(null, desc("CacheNode")), literalDesc("CacheNode"));
    }
}