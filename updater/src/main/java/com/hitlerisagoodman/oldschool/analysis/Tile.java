package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import com.hitlerisagoodman.modscript.hook.FieldHook;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"objects", "x", "y", "plane", "boundaryDecoration", "decoration", "boundary"})
public class Tile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("Z") == 3;
    }

    @Override
    public void visit() {
        add("objects", cn.getField(null, "[" + desc("InteractableEntity")), "[" + literalDesc("InteractableEntity"));
        add("decoration", cn.getField(null, desc("FloorDecoration")), literalDesc("FloorDecoration"));
        add("boundary", cn.getField(null, desc("Boundary")), literalDesc("Boundary"));
        add("boundaryDecoration", cn.getField(null, desc("BoundaryDecoration")), literalDesc("BoundaryDecoration"));
        visit(new TileHooks());
    }

    private class TileHooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(cn.name) && fmn.desc().equals("I")) {
                        VariableNode vn = (VariableNode) fmn.layer(IMUL, ILOAD);
                        if (vn == null) vn = (VariableNode) fmn.layer(DUP_X1, IMUL, ILOAD);
                        if (vn != null) {
                            String name = null;
                            if (vn.var() == 1) {
                                name = "plane";
                            } else if (vn.var() == 2) {
                                name = "x";
                            } else if (vn.var() == 3) {
                                name = "y";
                            }
                            if (name == null) return;
                            hooks.put(name, new FieldHook(name, fmn.fin()));
                            added++;
                        }
                    }
                }
            });
        }
    }
}


