package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.awt.*;

/**
 * Created by Asus on 25/05/2017.
 */
public class Producer extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(Component.class) == 1 && cn.fieldCount(Image.class) == 1
                && cn.fieldCount() == 2;
    }

    @Override
    public void visit() {

    }
}
