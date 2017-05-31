package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 07:05
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
@VisitorInfo(hooks = {"xVertices", "yVertices", "zVertices", "xTriangles", "yTriangles", "zTriangles"})
public class Model extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Entity")) && cn.fieldCount("[I") > 10;
    }

    @Override
    public void visit() {
        visit(new Hooks());
    }

    private class Hooks extends BlockVisitor {

        private int hooked = 0;

        @Override
        public boolean validate() {
            return hooked < 6;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() != ISTORE) return;
                    String name = null;
                    int var = vn.var();
                    switch (var) {
                        case 5:
                        case 6:
                        case 7:
                            name = "xyz".charAt(var - 5) + "Triangles";
                            break;
                        case 32:
                        case 33:
                        case 34:
                            name = "xyz".charAt(var - 32) + "Vertices";
                            break;
                    }
                    if (name != null) {
                        FieldMemberNode rn = (FieldMemberNode) vn.layer(IALOAD, GETFIELD);
                        if (rn != null && rn.owner().equals(cn.name) && rn.desc().equals("[I")) {
                            vn = rn.parent().firstVariable();
                            if (vn != null && (vn.var() == 1 || vn.var() == 31) && !hooks.containsKey(name)) {
                                addHook(new FieldHook(name, rn.fin()));
                                hooked++;
                            }
                        }
                    }
                }
            });
        }
    }
}
