package com.inubot.bot.modscript;

import com.inubot.bot.modscript.transform.Transform;
import com.inubot.bot.util.Configuration;
import com.inubot.bot.util.io.JarNode;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

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

    public Map<String, byte[]> inject(boolean dump) {
        getTransforms().forEach(t -> t.inject(arch.get()));
        Map<String, byte[]> out = new HashMap<>();
        for (ClassNode cn : arch.get().values()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(writer);
            out.put(cn.name, writer.toByteArray());
        }
        if (dump) {
            try (JarOutputStream output = new JarOutputStream(new FileOutputStream(Configuration.INJECTED), arch.getManifest())) {
                for (Map.Entry<String, byte[]> entry : out.entrySet()) {
                    output.putNextEntry(new JarEntry(entry.getKey() + ".class"));
                    output.write(entry.getValue());
                    output.closeEntry();
                }
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    public List<Transform> getTransforms() {
        return transforms;
    }
}
