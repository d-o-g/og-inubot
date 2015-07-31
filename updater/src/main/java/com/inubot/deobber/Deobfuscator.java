/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.modscript.hook.Hook;
import com.inubot.visitor.GraphVisitor;
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
                new DummyMethodVisitor(factories),// new OpaquePredicateVisitor() /* <- isnt 100% yet */
        };
        Arrays.asList(transforms).forEach(t -> {
            t.transform(nodes);
            System.out.println(t.toString());
        });
        System.out.printf("^ Executed deobfuscation transforms in %d millis%n", System.currentTimeMillis() - time);
        //TODO add a better renamer? sedlars Assembly thing is too slow
        for (ClassNode node : nodes.values()) {
            for (FieldNode fn : node.fields) {
                if (fn.name.equals("do") || fn.name.equals("if")) {
                    Assembly.rename(nodes.values(), fn, fn.name + "_");
                }
            }
            for (MethodNode mn : node.methods) {
                if (mn.name.equals("do") || mn.name.equals("if")) {
                    Assembly.rename(nodes.values(), mn, mn.name + "_");
                }
            }
        }
        for (ClassNode node : nodes.values()) {
            if (node.name.equals("do") || node.name.equals("if")) {
                Assembly.rename(nodes.values(), node, node.name + "_");
            }
        }
        getArchive().write(new File(output));
    }

    public JarArchive getArchive() {
        return archive;
    }
}
