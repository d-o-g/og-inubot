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

import java.util.List;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
@VisitorInfo(hooks = {"xVertices", "yVertices", "zVertices"})
public class OpenGLModel extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Model")) && cn.fieldCount(desc("OpenGLRenderConfiguration")) == 1;
    }

    @Override
    public void visit() {
        List<FieldHook> hooks = Model.findVertices(cn);
        if (hooks != null) {
            hooks.forEach(this::addHook);
        }
    }
}
