package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"height"})
public class Renderable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT && cn.superName.equals(clazz("CacheNode"));
    }

    @Override
    public void visit() {
        add("height", cn.getField(null, "I"), "I");
    }
}

