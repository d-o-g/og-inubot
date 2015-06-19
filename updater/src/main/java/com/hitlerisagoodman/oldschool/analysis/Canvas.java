package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {})
public class Canvas extends GraphVisitor {

    @Override
    public String iface() {
        return updater.getAccessorPackage() + "/input/Canvas";
    }

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals("java/awt/Canvas") && cn.fieldCount("Ljava/awt/Component;") == 1;
    }

    @Override
    public void visit() {
    }
}
