/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * @author Dogerina
 * @since 23-07-2015
 */
@VisitorInfo(hooks = {"absoluteX", "absoluteY", "multiplierX", "multiplierY"})
public class DirectXRenderConfiguration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("RenderConfiguration")) && (cn.access & ACC_ABSTRACT) != 0;
    }

    @Override
    public void visit() {
        List<FieldMemberNode> hooks = RenderConfiguration.findVertices(this);
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
