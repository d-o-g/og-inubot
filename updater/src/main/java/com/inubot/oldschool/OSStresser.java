/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.oldschool;

import com.inubot.Updater;
import com.inubot.oldschool.analysis.*;
import com.inubot.oldschool.analysis.PathingEntity;
import com.inubot.oldschool.analysis.Queue;
import com.inubot.visitor.GraphVisitor;
import com.inubot.util.Configuration;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

/**
 * @author unsigned
 * @since 23-04-2015
 */
public class OSStresser extends Updater {

    private static final boolean server = new File("/usr/share/nginx/html/data/").exists();

    public OSStresser(File file, GraphVisitor[] visitors, boolean closeOnOld) throws Exception {
        super(file, visitors, closeOnOld);
    }

    public OSStresser(File file, boolean closeOnOld) throws Exception {
        this(file, createVisitors(), closeOnOld);
    }

    private static GraphVisitor[] createVisitors() {
        return new GraphVisitor[]{
                new Node(), new DoublyLinkedNode(), new Entity(), new NodeTable(),
                new Cache(), new NodeDeque(), new Queue(), new Tile(), new Model(),
                new AnimationSequence(), new PathingEntity(), new NpcDefinition(), new Npc(),
                new Player(), new Item(), new ItemDefinition(), new EntityMarker(),
                new ObjectDefinition(), new Region(), new Canvas(), new InterfaceNode(),
                new CollisionMap(), new InterfaceComponent(), new Varps(), new Client()
        };
    }

    public static void main(String[] args) throws Exception {
        File dir = new File("./packs");
        if (!dir.exists())
            throw new RuntimeException("Invalid gamepack repository specified");
        List<File> gamepacks = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (!file.getName().endsWith(".jar"))
                continue;
            gamepacks.add(file);
        }

        System.out.printf("Gamepack repository size: %d%n", gamepacks.size());
        for (File pack : gamepacks) {
            Updater updater = new OSStresser(pack, false);
            updater.print = false;
            updater.run();
        }
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
        final AtomicInteger revision = new AtomicInteger(0);
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
}
