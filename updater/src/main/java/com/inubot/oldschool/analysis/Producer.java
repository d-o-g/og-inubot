package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.InvokeHook;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.tree.ClassNode;

import java.awt.*;

/**
 * Created by Asus on 25/05/2017.
 */
@VisitorInfo(hooks = {"drawGame"})
public class Producer extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(Component.class) == 1 && cn.fieldCount(Image.class) == 1
                && cn.fieldCount() == 2;
    }

    @Override
    public void visit() {
        visitLocalIfM(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitMethod(MethodMemberNode mmn) {
                        if (mmn.name().equalsIgnoreCase("drawImage")) {
                            if (mmn.hasChild(ILOAD)) {
                                addHook(new InvokeHook("drawGame", block.owner));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }, m -> m.desc.startsWith("(Ljava/awt/Graphics;II"));
    }
}
