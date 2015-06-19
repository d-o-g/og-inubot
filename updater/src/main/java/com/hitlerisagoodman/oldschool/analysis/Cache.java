package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {})
public class Cache extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.getFieldTypeCount() == 4 && cn.fieldCount(desc("CacheNode")) == 1 &&
                cn.fieldCount(desc("NodeTable")) == 1;
    }

    @Override
    public void visit() {
    }
}
