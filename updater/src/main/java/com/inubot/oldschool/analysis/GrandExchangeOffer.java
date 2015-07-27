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
import org.objectweb.asm.tree.*;

/**
 * @author Dogerina
 * @since 27-07-2015
 */
@VisitorInfo(hooks = {"itemId", "price", "itemQuantity", "transferred", "spent", "state"})
public class GrandExchangeOffer extends GraphVisitor {

    private static FieldInsnNode next(AbstractInsnNode from, int op, String desc, String owner, int skips) {
        int skipped = 0;
        while ((from = from.next()) != null) {
            if (from.opcode() == op) {
                FieldInsnNode topkek = (FieldInsnNode) from;
                if (topkek.desc.equals(desc) && (owner == null || owner.equals(topkek.owner))) {
                    if (skipped == skips) {
                        return topkek;
                    }
                    skipped++;
                }
            }
        }
        return null;
    }

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 6 && cn.fieldCount(int.class) == 5 && cn.fieldCount(byte.class) == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("state", cn.getField(null, "B")));
        for (ClassNode cn : updater.classnodes.values()) {
            for (MethodNode mn : cn.methods) {
                if (mn.desc.startsWith("(L")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain instanceof IntInsnNode) {
                            int oper = ((IntInsnNode) ain).operand;
                            FieldInsnNode fin = next(ain, GETFIELD, "I", this.cn.name, 0);
                            if (fin != null) {
                                if (oper == 3904) {
                                    addHook(new FieldHook("itemId", fin));
                                } else if (oper == 3905) {
                                    addHook(new FieldHook("price", fin));
                                } else if (oper == 3906) {
                                    addHook(new FieldHook("itemQuantity", fin));
                                } else if (oper == 3907) {
                                    addHook(new FieldHook("transferred", fin));
                                } else if (oper == 3908) {
                                    addHook(new FieldHook("spent", fin));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
