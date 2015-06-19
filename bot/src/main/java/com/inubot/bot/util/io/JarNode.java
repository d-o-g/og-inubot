package com.inubot.bot.util.io;

import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.ClassReader;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class JarNode {

    private final Map<String, ClassStructure> nodes = new HashMap<>();
    private final File file;

    public JarNode(File file) {
        this.file = file;
    }

    public Map<String, ClassStructure> get() {
        if (nodes.size() > 0)
            return nodes;
        try (final JarFile jf = new JarFile(file)) {
            final JarInputStream in = new JarInputStream(new FileInputStream(file));
            for (JarEntry entry = in.getNextJarEntry(); entry != null; entry = in.getNextJarEntry()) {
                final String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    final ClassReader cr = new ClassReader(jf.getInputStream(entry));
                    final ClassStructure cs = new ClassStructure();
                    cr.accept(cs, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                    nodes.put(entryName.replace(".class", ""), cs);
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return nodes;
    }
}
