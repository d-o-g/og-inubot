/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber;

import org.objectweb.asm.commons.cfg.transform.Transform;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dogerina
 * @since 31-07-2015
 */
public class CatchBlockVisitor extends Transform {

    private int kill = 0;

    @Override
    public void transform(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                List<TryCatchBlockNode> remove = mn.tryCatchBlocks.stream().filter(tcb ->
                        tcb.type != null && tcb.type.contains("Runtime")).collect(Collectors.toList());
                List<TryCatchBlockNode> skip = new ArrayList<>();
                for (TryCatchBlockNode tcb : remove) {
                    if (skip.contains(tcb)) {
                        mn.tryCatchBlocks.remove(tcb);
                        kill++;
                        continue;
                    }
                    skip.addAll(remove.stream().filter(check ->
                            check != tcb && check.handler == tcb.handler).collect(Collectors.toList()));
                    AbstractInsnNode cur = tcb.handler.next();
                    while (!isCodeKill(cur.opcode()))
                        cur = cur.next();
                    if (cur.opcode() == ATHROW) {
                        cur = tcb.handler.next();
                        while (!isCodeKill(cur.opcode())) {
                            AbstractInsnNode temp = cur;
                            cur = cur.next();
                            mn.instructions.remove(temp);
                        }
                        mn.instructions.remove(cur);
                        mn.tryCatchBlocks.remove(tcb);
                        kill++;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "^ Removed " + kill + " catch blocks";
    }

    private boolean isCodeKill(int opcode) {
        return (opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW;
    }
}
