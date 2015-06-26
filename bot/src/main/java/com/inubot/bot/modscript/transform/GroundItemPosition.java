/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform;

import jdk.internal.org.objectweb.asm.tree.*;
import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.hooks.FieldHook;
import com.inubot.bot.modscript.transform.util.ASMFactory;
import com.inubot.bot.modscript.transform.util.RIS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public class GroundItemPosition implements Transform {

    public static final Logger logger = LoggerFactory.getLogger(GroundItemPosition.class);

    private static final int[][] PATTERNS = {
            {
                    GETSTATIC, LDC, GETSTATIC, IMUL,
                    AALOAD, ILOAD, AALOAD, ILOAD,
                    AALOAD, ALOAD, INVOKEVIRTUAL
            },
            {
                    GETSTATIC, GETSTATIC, LDC, IMUL,
                    AALOAD, ILOAD, AALOAD, ILOAD,
                    AALOAD, ALOAD, INVOKEVIRTUAL
            }
    };

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        /* Insert fields */
        final String gi = ModScript.getClass("Item");
        final ClassNode groundItem = classes.get(gi);

        FieldNode xField = new FieldNode(ACC_PUBLIC, "strictX", "I", null, null);
        FieldNode yField = new FieldNode(ACC_PUBLIC, "strictY", "I", null, null);
        groundItem.fields.add(xField);
        groundItem.fields.add(yField);
        /* Insert getters for fields */
        groundItem.methods.add(ASMFactory.createGetter(false, gi, "strictX", "I", "strictX"));
        groundItem.methods.add(ASMFactory.createGetter(false, gi, "strictY", "I", "strictY"));

        FieldHook ground = ModScript.getFieldHook("Client#groundItems");
        FieldHook floor = ModScript.getFieldHook("Client#plane");
        if (ground == null || floor == null)
            throw new RuntimeException("Dependancy error");
        /* Insert field setters */
        for (final ClassNode cn : classes.values()) {
            out:
            for (MethodNode mn : cn.methods) {
                if (mn.desc.endsWith("V") && (mn.desc.indexOf(')') - mn.desc.indexOf('(')) <= 2) {
                    RIS searcher = new RIS(mn.instructions);
                    List<AbstractInsnNode[]> matches = new ArrayList<>();
                    for (int[] pattern : PATTERNS)
                        matches.addAll(searcher.search(RIS.mkPattern(pattern)));
                    // deque[z][x][y].add(groundItem)
                    for (AbstractInsnNode[] match : matches) {
                        FieldInsnNode g = (FieldInsnNode) match[0];
                        if (g.owner.equals(ground.clazz)
                                && g.name.equals(ground.field)
                                && g.desc.equals(ground.fieldDesc)) {
                            int x = ((VarInsnNode) match[5]).var;
                            int y = ((VarInsnNode) match[7]).var;
                            int item = ((VarInsnNode) match[9]).var;
                            InsnList code = new InsnList();
                            // item.x = local_x >> 7 + 64
                            code.add(new VarInsnNode(ALOAD, item));
                            code.add(new VarInsnNode(ILOAD, x));
                            code.add(new IntInsnNode(BIPUSH, 7));
                            code.add(new InsnNode(ISHL));
                            code.add(new IntInsnNode(BIPUSH, 64));
                            code.add(new InsnNode(IADD));
                            code.add(new FieldInsnNode(PUTFIELD, gi, "strictX", "I"));

                            // item.y = local_y >> 7 + 64
                            code.add(new VarInsnNode(ALOAD, item));
                            code.add(new VarInsnNode(ILOAD, y));
                            code.add(new IntInsnNode(BIPUSH, 7));
                            code.add(new InsnNode(ISHL));
                            code.add(new IntInsnNode(BIPUSH, 64));
                            code.add(new InsnNode(IADD));
                            code.add(new FieldInsnNode(PUTFIELD, gi, "strictY", "I"));
                            mn.instructions.insert(match[10], code);
                            logger.debug("Successfully injected GroundItemPosition");
                            continue out;
                        }
                    }
                }
            }
        }
    }
}
