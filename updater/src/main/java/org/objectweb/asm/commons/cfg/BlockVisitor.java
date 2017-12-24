package org.objectweb.asm.commons.cfg;

import org.objectweb.asm.Opcodes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Tyler Sedlar
 */
public abstract class BlockVisitor implements Opcodes {

    public AtomicBoolean lock = new AtomicBoolean(false);

    public abstract boolean validate();

    public final void visitInternal(Block block) {
        //System.out.println(getClass().getSimpleName());
        visit(block);
    }

    public abstract void visit(Block block);

    public void visitEnd() {}
}
