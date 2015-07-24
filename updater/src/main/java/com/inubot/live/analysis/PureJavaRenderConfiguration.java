/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.modscript.hook.InvokeHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.*;

import java.util.List;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"absoluteX", "absoluteY", "multiplierX", "multiplierY", "update"})
public class PureJavaRenderConfiguration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (!cn.superName.equals(clazz("RenderConfiguration"))) {
            return false;
        }
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
        InvokeHook update = RenderConfiguration.findUpdate(this);
        if (update != null) {
            addHook(update);
        }
        List<FieldMemberNode> hooks = RenderConfiguration.findHooks(this);
        if (hooks != null) {
            while (hooks.size() > 4) {
                hooks.remove(0);
            }
            for (FieldMemberNode hook : hooks) {
                NumberNode index = (NumberNode) hook.top().first(ICONST_0);
                if (index == null) {
                    index = (NumberNode) hook.top().first(ICONST_1);
                }
                if (index == null) {
                    continue;
                }
                String hookName = hook.parent().opcode() == FADD ? "absolute" : "multiplier";
                hookName += index.number() == 0 ? "X" : "Y";
                addHook(new FieldHook(hookName, hook.fin()));
            }
        }
    }
}
