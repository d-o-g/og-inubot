/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author unsigned
 * @since 22-04-2015
 */
@VisitorInfo(hooks = "flags")
public class CollisionMap extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getFieldTypeCount() == 2 && cn.fieldCount("[[I") == 1 && cn.fieldCount("I") == 4;
    }

    @Override
    public void visit() {
        add("flags", cn.getField(null, "[[I"));
    }
}
