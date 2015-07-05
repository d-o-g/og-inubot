/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 05-07-2015
 */
public class SceneGraph extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.ownerless() && cf.fieldCount("[[[" + desc("SceneGraphTile")) > 0;
    }

    @Override
    public void visit() {

    }
}