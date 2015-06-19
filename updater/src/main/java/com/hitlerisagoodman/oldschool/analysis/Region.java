package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

@VisitorInfo(hooks = {"tiles", "objects"})
public class Region extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.getAbnormalFieldCount() == 2 &&
                cn.fieldCount("[[[" + desc("Tile")) == 1 && cn.fieldCount("[" + desc("InteractableEntity")) == 1;
    }

    @Override
    public void visit() {
        add("tiles", cn.getField(null, "[[[" + desc("Tile")), "[[[" + literalDesc("Tile"));
        add("objects", cn.getField(null, "[" + desc("InteractableEntity")), "[" + literalDesc("InteractableEntity"));
    }
}

