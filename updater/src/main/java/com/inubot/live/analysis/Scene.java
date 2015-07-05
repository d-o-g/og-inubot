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

import java.util.HashMap;

/**
 * @author Dogerina
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"settings"})
public class Scene extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.fieldCount(HashMap.class) == 1 && cf.ownerless() && cf.fieldCount(byte[][].class) > 3;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("settings", cn.getField(null, desc("SceneSettings"))));
    }
}
