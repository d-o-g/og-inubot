package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"value"})
public class IntegerNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equalsIgnoreCase(clazz("Node"))
                && cn.fieldCount() == 1
                && cn.fieldCount(int.class) == 1
                && (cn.access & ACC_ABSTRACT) == 0;
    }

    @Override
    public void visit() {
        add("value", cn.getField(null, "I"));
    }
}
