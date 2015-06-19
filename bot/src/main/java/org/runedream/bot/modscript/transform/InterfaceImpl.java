package org.runedream.bot.modscript.transform;

import org.runedream.bot.modscript.ModScript;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import org.runedream.bot.modscript.asm.ClassStructure;

import java.util.Map;

public class InterfaceImpl implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        for (ClassNode cn : classes.values()) {
            String def = ModScript.getDefinedName(cn.name);
            if (def != null)
                cn.interfaces.add("org/runedream/client/natives/RS" + def);
        }
    }
}
