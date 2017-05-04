package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by Inspiron on 14/07/2016.
 */
@VisitorInfo(hooks = {"definition", "hitsplats"})
public class HealthBar extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("NodeIterable")) == 1 && cn.fieldCount(desc("HealthBarDefinition")) == 1;
    }

    @Override
    public void visit() {
        add("definition", cn.getField(null, desc("HealthBarDefinition")));
        add("hitsplats", cn.getField(null, desc("NodeIterable")));
    }
}