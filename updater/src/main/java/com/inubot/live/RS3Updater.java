/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live;

import com.inubot.Updater;
import com.inubot.live.analysis.Client;
import com.inubot.live.analysis.Widget;
import com.inubot.util.io.Crawler;
import com.inubot.visitor.GraphVisitor;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

public class RS3Updater extends Updater {

    @Override
    public String getType() {
        return "RuneScape";
    }

    @Override
    public String getAccessorPrefix() {
        return "com/inubot/client/natives/modern/RS";
    }

    @Override
    public String getWrapperPrefix() {
        return "com/inubot/api/live";
    }

    @Override
    public int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs) {
        ClassNode client = null;
        for (ClassNode cn : graphs.keySet()) {
            if (cn.name.equals("Rs2Applet"))
                continue;
            if (!cn.ownerless() && cn.getMethodByName("init") != null) {
                client = cn;
                break;
            }
        }
        if (client == null)
            throw new RuntimeException("Cannot find client");
        MethodNode init = client.getMethodByName("init");
        FlowGraph graph = graphs.get(client).get(init);
        AtomicInteger revision = new AtomicInteger(0);
        for (Block block : graph) {
            block.tree().accept(new NodeVisitor() {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEVIRTUAL && mmn.desc().contains("Ljava/lang/String;IIII")) {
                        NumberNode nn = (NumberNode) mmn.first(SIPUSH);
                        if (nn != null)
                            revision.set(nn.number());
                    }
                }
            });
        }
        return revision.get();
    }

    @Override
    public String getModscriptLocation() {
        return "live.dat";
    }

    @Override
    public String getHash() {
        try (JarFile jar = new JarFile(file)) {
            return Integer.toString(jar.getManifest().hashCode());
        } catch (IOException | NullPointerException e) {
            return file.getName().replace(".jar", "");
        }
    }

    private static GraphVisitor[] createVisitors() {
        return new GraphVisitor[]{new Widget(), new Client()};
    }

    public RS3Updater(File file) throws Exception {
        super(file, createVisitors(), false);
    }

    private static void run(boolean global, boolean print, File pack) throws Exception {
        if (global) {
            File[] packs = new File("packs/rs3/843/").listFiles();
            if (packs != null && packs.length > 0) {
                for (File p : packs) {
                    RS3Updater updater = new RS3Updater(p);
                    updater.print = print;
                    updater.run();
                    updater.flush();
                    System.gc();
                }
            }
        } else {
            if (pack == null) {
                Crawler crawler = new Crawler(Crawler.GameType.RS3);
                crawler.crawl();
                if (crawler.outdated()) {
                    crawler.download();
                    pack = new File(crawler.pack);
                    new Unpacker(pack, crawler.parameters.get("0"), crawler.parameters.get("-1")).dump(pack);
                } else {
                    pack = new File(crawler.pack);
                }
            }
            RS3Updater updater = new RS3Updater(pack);
            updater.print = print;
            updater.run();
            updater.flush();
            System.gc();
        }
    }

    private static void run(boolean global, boolean print) throws Exception {
        run(global, print, null);
    }

    public static void main(String[] args) throws Exception {
        run(false, true);
    }
}

