package com.inubot.oldschool.analysis;


import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

/**
 * Created by Inspiron on 14/07/2016.
 */
public class HealthBarDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("DoublyLinkedNode")) && cn.fieldCount() == 10 && cn.fieldCount(int.class) == 10;
    }

    @Override
    public void visit() {

    }
}