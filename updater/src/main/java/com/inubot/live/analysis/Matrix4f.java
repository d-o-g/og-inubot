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
import org.objectweb.asm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"matrix"})
public class Matrix4f extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_FINAL) != 0 && cn.getFieldTypeCount() == 1 && cn.fieldCount(float[].class) == 1
                && cn.getMethod("toString", "()Ljava/lang/String;") != null; //ayyyy lmao
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            addHook(new FieldHook("matrix", fn));
        }
    }
}
