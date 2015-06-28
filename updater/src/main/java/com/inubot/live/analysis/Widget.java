/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 28-06-2015
 */
@VisitorInfo(hooks = {})
public class Widget extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount("[Ljava/lang/Object;") > 15;
    }

    @Override
    public void visit() {

    }
}
