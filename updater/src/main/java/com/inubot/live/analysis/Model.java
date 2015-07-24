/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.util.RIS;
import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public class Model extends GraphVisitor {

    public static List<FieldHook> findVertices(ClassNode cn) {
        List<FieldHook> hooks = new ArrayList<>(3);
        for (MethodNode mn : cn.methods) {
            if (Modifier.isStatic(mn.access)) {
                continue;
            }
            RIS ris = new RIS(mn.instructions);
            List<AbstractInsnNode[]> nodeList = ris.search("aload getfield iload aload getfield");
            if (nodeList.size() == 3) {
                FieldInsnNode x = (FieldInsnNode) nodeList.get(0)[1];
                FieldInsnNode y = (FieldInsnNode) nodeList.get(1)[1];
                FieldInsnNode z = (FieldInsnNode) nodeList.get(2)[1];
                if (x.desc.equals("[I") && y.desc.equals("[I") && z.desc.equals("[I")) {
                    hooks.add(new FieldHook("xVertices", x));
                    hooks.add(new FieldHook("yVertices", y));
                    hooks.add(new FieldHook("zVertices", z));
                }
            }
        }
        return hooks.size() < 3 ? null : hooks;
    }

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_ABSTRACT) != 0 && cn.getFieldTypeCount() == 1 && cn.fieldCount("Z") == 2
                && cn.fieldAccessCount(ACC_PROTECTED) == 1;
    }

    @Override
    public void visit() {

    }
}
