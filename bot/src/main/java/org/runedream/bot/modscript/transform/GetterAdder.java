package org.runedream.bot.modscript.transform;

import org.runedream.bot.modscript.ModScript;
import org.runedream.bot.modscript.asm.ClassStructure;
import org.runedream.bot.modscript.hooks.FieldHook;
import org.runedream.bot.modscript.transform.util.ASMFactory;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public class GetterAdder implements Transform {
    @Override
    public void inject(Map<String, ClassStructure> classes) {
        for (FieldHook hook : ModScript.FIELD_HOOK_MAP.values()) {
            ClassNode where = classes.get(hook.isStatic ? "client" : hook.clazz);
            if (where == null)
                continue;
            String retDesc = hook.fieldDesc.replace("[", "").replace(";", "");
            if (retDesc.startsWith("L") && !retDesc.contains("java") && !retDesc.contains("/")) {
                String prebuild = "";
                for (char c : hook.fieldDesc.toCharArray()) {
                    if (c == '[') {
                        prebuild += '[';
                    }
                }
                retDesc = retDesc.replace("L", "");
                retDesc = prebuild + "Lorg/runedream/client/natives/RS" + ModScript.getDefinedName(retDesc) + ";";
                if (retDesc.contains("null"))
                    retDesc = hook.fieldDesc;
            } else {
                retDesc = hook.fieldDesc;
            }
            where.methods.add(ASMFactory.createGetter(hook, retDesc));
        }
    }
}
