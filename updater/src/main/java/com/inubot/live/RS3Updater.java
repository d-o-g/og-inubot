/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.live;

import com.inubot.Updater;
import com.inubot.live.analysis.*;
import com.inubot.util.Configuration;
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

    private static final boolean server = new File("/usr/share/nginx/html/data/").exists();

    public RS3Updater(File file) throws Exception {
        super(file, createVisitors(), false);
    }

    private static GraphVisitor[] createVisitors() {
        return new GraphVisitor[]{new Node(), new DoublyNode(), new NodeTable(),
                new DoublyNodeQueue(), new Cache(),
                new Canvas(), new RenderMode(), new RenderConfiguration(), new PureJavaRenderConfiguration(),
                new Vector3f(), new Quaternion(), new CoordinateSpace(), new SceneGraphTile(),
                new SceneGraph(), new Scene(), new SceneOffset(),
                new SceneSettings(), new SceneGraphLevel(),
                new GuidanceArrow(),
                new MenuItem(),
                new Widget(), new Client()};
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
        }
        System.gc();
    }

    private static void run(boolean global, boolean print) throws Exception {
        run(global, print, null);
    }

    public static void main(String[] args) throws Exception {
        run(false, true);
    }

    @Override
    public String getType() {
        return "modern";
    }

    @Override
    public String getAccessorPrefix() {
        return "com/inubot/client/natives/modern/RS";
    }

    @Override
    public String getWrapperPrefix() {
        return "com/inubot/api/modern";
    }

    @Override
    public int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs) {
        ClassNode client = null;
        for (ClassNode cn : graphs.keySet()) {
            if (!cn.name.equals("Rs2Applet") && !cn.ownerless() && cn.getMethodByName("init") != null) {
                client = cn;
                break;
            }
        }
        if (client == null) {
            throw new RuntimeException("Cannot find client");
        }
        MethodNode init = client.getMethodByName("init");
        FlowGraph graph = graphs.get(client).get(init);
        AtomicInteger revision = new AtomicInteger(0);
        for (Block block : graph) {
            block.tree().accept(new NodeVisitor() {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEVIRTUAL && mmn.desc().contains("Ljava/lang/String;IIII")) {
                        NumberNode nn = (NumberNode) mmn.first(SIPUSH);
                        if (nn != null) {
                            revision.set(nn.number());
                        }
                    }
                }
            });
        }
        return revision.get();
    }

    @Override
    public String getModscriptLocation() {
        /**
         * TODO dont forget to change back
         return server ? "/usr/share/nginx/html/data/modern" + getHash() + ".dat"
         : Configuration.CACHE + "/modern" + getHash() + ".dat";
         */
        return server ? "/usr/share/nginx/html/data/modern.dat" : Configuration.CACHE + "/modern.dat";
    }

    @Override
    public String getHash() {
        try (JarFile jar = new JarFile(file)) {
            return Integer.toString(jar.getManifest().hashCode());
        } catch (IOException | NullPointerException e) {
            return file.getName().replace(".jar", "");
        }
    }
}

