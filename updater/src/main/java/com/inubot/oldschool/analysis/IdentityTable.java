package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by Asus on 01/06/2017.
 */
@VisitorInfo(hooks = {"contents"})
public class IdentityTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getFieldTypeCount() == 1 && cn.fieldCount(int[].class) == 1;
    }

    @Override
    public void visit() {
        add("contents", cn.getField(null, "[I"));
    }
}
