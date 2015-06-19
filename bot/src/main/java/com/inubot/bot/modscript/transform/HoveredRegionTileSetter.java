/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.tree.*;
import com.inubot.bot.modscript.hooks.FieldHook;

import java.util.Map;

/**
 * @author unsigned
 * @since 24-04-2015
 */
public class HoveredRegionTileSetter implements Transform {

    @Override
    public void inject(Map<String, ClassStructure> classes) {
        FieldHook xHook = ModScript.getFieldHook("Client#hoveredRegionTileX");
        FieldHook yHook = ModScript.getFieldHook("Client#hoveredRegionTileY");
        if (xHook == null || yHook == null)
            throw new RuntimeException("hook broke?");

        MethodNode xSetter = new MethodNode(ACC_PUBLIC, "setHoveredRegionTileX", "(I)V", null, null);
        xSetter.instructions.add(new VarInsnNode(ILOAD, 1));
        xSetter.instructions.add(new FieldInsnNode(PUTSTATIC, xHook.clazz, xHook.field, xHook.fieldDesc));
        xSetter.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(xSetter);

        MethodNode ySetter = new MethodNode(ACC_PUBLIC, "setHoveredRegionTileY", "(I)V", null, null);
        ySetter.instructions.add(new VarInsnNode(ILOAD, 1));
        ySetter.instructions.add(new FieldInsnNode(PUTSTATIC, yHook.clazz, yHook.field, yHook.fieldDesc));
        ySetter.instructions.add(new InsnNode(RETURN));
        classes.get("client").methods.add(ySetter);
    }
}
