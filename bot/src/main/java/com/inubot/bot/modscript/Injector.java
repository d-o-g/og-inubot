package com.inubot.bot.modscript;

import com.inubot.bot.modscript.transform.Transform;
import com.inubot.bot.modscript.transform.Transform;
import com.inubot.bot.util.io.JarNode;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.util.*;

/**
 * @author unsigned
 * @since 20-04-2015
 */
public class Injector {

    private final JarNode arch;
    private final List<Transform> transforms;

    public Injector(final JarNode arch) {
        this.arch = arch;
        this.transforms = new ArrayList<>();
    }

    public Map<String, byte[]> inject() {
        getTransforms().forEach(t -> t.inject(arch.get()));
        Map<String, byte[]> out = new HashMap<>();
        for (ClassNode cn : arch.get().values()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(writer);
            out.put(cn.name, writer.toByteArray());
        }
        return out;
    }

    public List<Transform> getTransforms() {
        return transforms;
    }
}
