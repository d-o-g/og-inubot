package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by Asus on 01/06/2017.
 */
@VisitorInfo(hooks = {"arguments"})
public class ScriptEvent extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(Object[].class) == 1 && cn.fieldCount(int.class) == 6;
    }

    @Override
    public void visit() {
        add("arguments", cn.getField(null, "[Ljava/lang/Object;"));
    }
}
