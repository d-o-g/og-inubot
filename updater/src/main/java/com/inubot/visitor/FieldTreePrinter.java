package com.inubot.visitor;

import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;

/**
 * @author Dogerina
 * @since 26-08-2015
 */
public class FieldTreePrinter extends BlockVisitor {

    private final String owner, name;

    public FieldTreePrinter(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void visit(Block block) {
        block.tree().accept(new NodeVisitor() {
            @Override
            public void visitField(FieldMemberNode fmn) {
                if (fmn.owner().equals(owner) && fmn.name().equals(name)) {
                    System.out.println("> " + block.owner.key());
                    System.out.println(fmn.tree());
                    System.out.println();
                }
            }
        });
    }
}

