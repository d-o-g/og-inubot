/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber;

import org.objectweb.asm.commons.cfg.transform.Transform;
import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.commons.util.JarArchive;
import org.objectweb.asm.commons.wrapper.ClassFactory;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.util.*;

/**
 * @author Dogerina
 * @since 31-07-2015
 */
public class Deobfuscator {

    private final JarArchive archive;
    private final String output;

    private Deobfuscator(String input, String output) {
        this.archive = new JarArchive(new File(input));
        this.output = output;
    }

    public static void main(String... args) {
        Deobfuscator deobfuscator = new Deobfuscator("C:\\Users\\Inspiron\\Documents\\inubot\\cache\\gamepack.jar",
                "C:\\Users\\Inspiron\\Documents\\inubot\\cache\\gamepack_deob.jar");
        deobfuscator.run();
    }

    private void run() {
        long time = System.currentTimeMillis();
        Map<String, ClassNode> nodes = getArchive().build();
        Map<String, ClassFactory> factories = new HashMap<>(nodes.size());
        for (ClassNode node : nodes.values()) {
            factories.put(node.name, new ClassFactory(node));
        }
        System.out.printf("^ Created tree of size %d in %d millis%n",
                factories.size(), System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        Transform[] transforms = {
                new DummyMethodVisitor(factories), new CatchBlockVisitor(),
        };
        Arrays.asList(transforms).forEach(t -> {
            t.transform(nodes);
            System.out.println(t.toString());
        });
        System.out.printf("^ Executed deobfuscation transforms in %d millis%n", System.currentTimeMillis() - time);
        getArchive().write(new File(output));
    }

    public JarArchive getArchive() {
        return archive;
    }
}
