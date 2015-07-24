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
 * @since 24-07-2015
 */
@VisitorInfo(hooks = {"renderConfiguration"})
public class DirectXSceneGraphLevel extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("SceneGraphLevel")) && cn.getField(null, desc("DirectXRenderConfiguration")) != null;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("renderConfiguration", cn.getField(null, desc("DirectXRenderConfiguration"))));
    }
}
