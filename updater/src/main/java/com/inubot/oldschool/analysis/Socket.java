package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * Created by Asus on 27/05/2017.
 */
@VisitorInfo(hooks = {"base"})
public class Socket extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        boolean inputstream = false;
        boolean outputstream = false;
        for (FieldNode field : cn.fields) {
            if ((field.access & ACC_STATIC) == 0 && field.desc.contains("InputStream"))
                inputstream = true;
            if ((field.access & ACC_STATIC) == 0 && field.desc.contains("OutputStream"))
                outputstream = true;
        }
        return inputstream && outputstream;
    }

    @Override
    public void visit() {
        add("base", cn.getField(null, "Ljava/net/Socket;"));
    }
}
