/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.tree.*;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
public class PureJavaRenderConfiguration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.opcode() == LDC && ((LdcInsnNode) ain).cst.equals("Pure Java")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void visit() {

    }
}
