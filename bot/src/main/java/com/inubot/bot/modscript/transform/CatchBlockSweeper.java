/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform;

import com.inubot.Inubot;
import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author unsigned
 * @since 27-06-2015
 */
public class CatchBlockSweeper implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        for (ClassStructure cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                List<TryCatchBlockNode> remove = mn.tryCatchBlocks.stream().filter(tcb ->
                        tcb.type != null && tcb.type.contains("Runtime")).collect(Collectors.toList());
                List<TryCatchBlockNode> skip = new ArrayList<>();
                for (TryCatchBlockNode tcb : remove) {
                    if (skip.contains(tcb)) {
                        mn.tryCatchBlocks.remove(tcb);
                        catchblockkills++;
                        continue;
                    }
                    skip.addAll(remove.stream().filter(check ->
                            check != tcb && check.handler == tcb.handler).collect(Collectors.toList()));
                    AbstractInsnNode cur = tcb.handler.getNext();
                    while (!isCodeKill(cur.getOpcode()))
                        cur = cur.getNext();
                    if (cur.getOpcode() == ATHROW) {
                        cur = tcb.handler.getNext();
                        while (!isCodeKill(cur.getOpcode())) {
                            AbstractInsnNode temp = cur;
                            cur = cur.getNext();
                            mn.instructions.remove(temp);
                        }
                        mn.instructions.remove(cur);
                        mn.tryCatchBlocks.remove(tcb);
                        catchblockkills++;
                    }
                }
            }
        }
        System.out.println(" # Removed " + catchblockkills + " catch blocks");
    }

    private int catchblockkills = 0;

    private static boolean isCodeKill(int opcode) {
        return (opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW;
    }
}
