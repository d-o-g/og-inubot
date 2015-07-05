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

import java.lang.reflect.Modifier;

/**
 * @author Dogerina
 * @since 05-07-2015
 * a 'plane' on the scenegraph
 */
@VisitorInfo(hooks = {"tileHeights"})
public class SceneGraphLevel extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.ownerless() && Modifier.isAbstract(cf.access)
                && cf.fieldCount(int.class) == 4 && cf.getAbnormalFieldCount() == 0;
    }

    @Override
    public void visit() {
        FieldNode fn = cn.getField(null, "[[I");
        if (fn == null)
            return;
        addHook(new FieldHook("tileHeights", fn));
    }
}
