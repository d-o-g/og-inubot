/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"doublyNodeQueue", "nodeTable"})
public class Cache extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getFieldTypeCount() == 4 && cn.fieldCount(desc("DoublyNodeQueue")) == 1 &&
                cn.fieldCount(desc("NodeTable")) == 1 && cn.fieldCount(desc("DoublyNode")) == 0;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("doublyNodeQueue", cn.getField(null, desc("DoublyNodeQueue"))));
        addHook(new FieldHook("nodeTable", cn.getField(null, desc("NodeTable"))));
    }
}
