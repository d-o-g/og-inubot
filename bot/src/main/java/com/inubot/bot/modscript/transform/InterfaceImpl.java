package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.asm.ClassStructure;
import com.inubot.bot.modscript.ModScript;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import com.inubot.bot.modscript.asm.ClassStructure;

import java.util.Map;

public class InterfaceImpl implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        for (ClassNode cn : classes.values()) {
            String def = ModScript.getDefinedName(cn.name);
            if (def != null)
                cn.interfaces.add("com/inubot/client/natives/RS" + def);
        }
    }
}
