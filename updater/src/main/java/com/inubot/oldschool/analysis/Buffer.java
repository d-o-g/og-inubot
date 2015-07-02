/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author unsigned
 * @since 06-05-2015
 */
@VisitorInfo(hooks = {"caret", "payload", "writeShort", "writeLEShort"})
public class Buffer extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 2 && cn.fieldCount("I") == 1 && cn.fieldCount("[B") == 1 && !cn.ownerless();
    }

    @Override
    public void visit() {
        addHook(new FieldHook("caret", cn.getField(null, "I")));
        addHook(new FieldHook("payload", cn.getField(null, "[B")));
    }
}
