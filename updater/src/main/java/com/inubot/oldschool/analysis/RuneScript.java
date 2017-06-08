package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by Asus on 03/06/2017.
 */
public class RuneScript extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("DoublyLinkedNode"))
                && cn.fieldCount(int.class) == 4
                && cn.fieldCount(int[].class) == 2
                && cn.fieldCount(String[].class) == 1;
    }

    @Override
    public void visit() {

    }
}
