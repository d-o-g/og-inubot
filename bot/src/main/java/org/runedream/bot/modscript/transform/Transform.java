package org.runedream.bot.modscript.transform;

import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import org.runedream.bot.modscript.asm.ClassStructure;

import java.util.Map;

public interface Transform extends Opcodes {

    String PACKAGE = "org/runedream/client/natives/";

    void inject(Map<String, ClassStructure> classes);
}
