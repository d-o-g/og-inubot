package com.inubot.bot.util.io;

import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.ClassReader;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.*;

public class JarNode {

    private final Map<String, ClassStructure> nodes = new HashMap<>();
    private final File file;
    private final Map<String, byte[]> classes;
    private Manifest manifest;

    public JarNode(File file) {
        this.file = file;
        this.classes = null;
    }

    public JarNode(Map<String, byte[]> classes) {
        this.classes = classes;
        this.file = null;
    }

    public Map<String, ClassStructure> get() {
        if (nodes.size() > 0)
            return nodes;
        if (file != null) {
            try (JarFile jf = new JarFile(file)) {
                try (JarInputStream in = new JarInputStream(new FileInputStream(file))) {
                    manifest = in.getManifest();
                    JarEntry entry = in.getNextJarEntry();
                    while (entry != null) {
                        String entryName = entry.getName();
                        if (entryName.endsWith(".class")) {
                            ClassReader cr = new ClassReader(jf.getInputStream(entry));
                            ClassStructure cs = new ClassStructure();
                            cr.accept(cs, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                            nodes.put(entryName.replace(".class", ""), cs);
                        }
                        entry = in.getNextJarEntry();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (classes != null) {
            for (Map.Entry<String, byte[]> clazz : classes.entrySet()) {
                if (!clazz.getKey().contains(".class"))
                    continue;
                ClassReader cr = new ClassReader(clazz.getValue());
                ClassStructure cs = new ClassStructure();
                cr.accept(cs, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                nodes.put(cs.name, cs);
            }
        } else {
            throw new IllegalStateException("wut");
        }
        return nodes;
    }

    public Manifest getManifest() {
        return manifest;
    }
}
