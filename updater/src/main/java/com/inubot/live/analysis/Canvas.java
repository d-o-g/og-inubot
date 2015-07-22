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
 * @since 21-07-2015
 */
public class Canvas extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals("java/awt/Canvas");
    }

    @Override
    public void visit() {

    }
}