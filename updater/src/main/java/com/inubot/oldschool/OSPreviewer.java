/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.oldschool;

import com.inubot.Updater;
import com.inubot.visitor.GraphVisitor;
import com.inubot.util.Configuration;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

/**
 * @author unsigned
 * @since 23-04-2015
 */
public class OSPreviewer extends Updater {

    private final HookPreview hookPreview = new HookPreview("ct.al");

    public OSPreviewer(File file, GraphVisitor[] visitors, boolean closeOnOld) throws Exception {
        super(file, visitors, closeOnOld);
    }

    public OSPreviewer(File file, boolean closeOnOld) throws Exception {
        this(file, new GraphVisitor[0], closeOnOld);
    }

    public static void main(String[] args) throws Exception {
        OSPreviewer updater = new OSPreviewer(null, false);
        updater.print = true;
        updater.run();
        System.out.printf("Executing hook previewer for field %s...%n", updater.hookPreview.key);
        for (ClassNode cn : updater.classnodes.values())
            cn.methods.forEach(updater.hookPreview::analyse);
    }

    @Override
    public String getType() {
        return "Oldschool RuneScape";
    }

    @Override
    public String getHash() {
        try (JarFile jar = new JarFile(file)) {
            return Integer.toString(jar.getManifest().hashCode());
        } catch (IOException | NullPointerException e) {
            return file.getName().replace(".jar", "");
        }
    }

    @Override
    public String getAccessorPrefix() {
        return "com/inubot/client/natives/oldschool/RS";
    }

    @Override
    public String getWrapperPrefix() {
        return "com/inubot/api/oldschool/";
    }

    @Override
    public String getModscriptLocation() {
        return null; //no modscript should be output for stress testing, therefore return null
    }

    @Override
    public int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs) {
        ClassNode client = classnodes.get("client");
        MethodNode init = client.getMethodByName("init");
        FlowGraph graph = graphs.get(client).get(init);
        AtomicInteger revision = new AtomicInteger(0);
        for (Block block : graph) {
            new BlockVisitor() {
                public boolean validate() {
                    return revision.get() == 0;
                }

                public void visit(Block block) {
                    block.tree().accept(new NodeVisitor(this) {
                        public void visitNumber(NumberNode nn) {
                            if (nn != null && nn.opcode() == SIPUSH) {
                                if ((nn = nn.nextNumber()) != null && nn.opcode() == SIPUSH) {
                                    if ((nn = nn.nextNumber()) != null) {
                                        revision.set(nn.number());
                                    }
                                }
                            }
                        }
                    });
                }
            }.visit(block);
        }
        return revision.get();
    }

    static {
        Configuration.setup();
    }

    private class HookPreview extends BlockVisitor {

        private final String key;
        private final boolean ignoreStaticMethods;

        /**
         * @param key                 The key of the field to find references for. E.G: fk.u
         * @param ignoreStaticMethods true if you only want to print references of the field being used in a non static context
         */
        private HookPreview(String key, boolean ignoreStaticMethods) {
            this.key = key;
            this.ignoreStaticMethods = ignoreStaticMethods;
        }

        private HookPreview(String key) {
            this(key, false);
        }

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (!fmn.key().equals(key))
                        return;
                    MethodNode mn = fmn.method();
                    System.out.println(mn.owner.name + "." + mn.name + mn.desc);
                    System.out.println(fmn.tree());
                    System.out.println();
                }
            });
        }

        private void analyse(MethodNode mn) {
            if (ignoreStaticMethods && (mn.access & ACC_STATIC) != 0)
                return;
            FlowGraph graph = graphs.get(mn.owner).get(mn);
            for (Block block : graph)
                visit(block);
        }
    }
}
