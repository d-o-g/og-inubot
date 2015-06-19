package com.hitlerisagoodman.oldschool.analysis;

import com.hitlerisagoodman.visitor.GraphVisitor;
import com.hitlerisagoodman.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 07:05
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
@VisitorInfo(hooks = {})
public class Model extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Renderable")) && cn.fieldCount("[I") > 10;
    }

    @Override
    public void visit() {

    }
}
