package com.inubot;

import com.inubot.constraint.InverseVisitor;
import com.inubot.constraint.OpaquePredicateVisitor;
import com.inubot.live.RS3Updater;
import com.inubot.modscript.ModScript;
import com.inubot.modscript.hook.*;
import com.inubot.util.io.Crawler;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.FlowVisitor;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.commons.cfg.transform.UnusedMethodTransform;
import org.objectweb.asm.commons.util.Assembly;
import org.objectweb.asm.commons.util.JarArchive;
import org.objectweb.asm.commons.wrapper.ClassFactory;
import org.objectweb.asm.commons.wrapper.ClassMethod;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tyler Sedlar
 */
public abstract class Updater extends Thread implements Runnable {

    public final File file;
    public final String hash;
    public final StringBuilder builder = new StringBuilder();
    public boolean print = false;
    public JarArchive archive;
    public Map<String, ClassNode> classnodes;
    public GraphVisitor[] visitors;
    public InverseVisitor inverseVisitor;
    public int revision;
    public String callbacks, classes, hooks;
    protected Map<ClassNode, Map<MethodNode, FlowGraph>> graphs = new HashMap<>();
    private boolean updated = false;

    public Updater(File file, GraphVisitor[] visitors, boolean closeOnOld) throws Exception {
        if (file == null) {
            Crawler crawler = new Crawler(Crawler.GameType.OSRS);
            crawler.crawl();
            if ((updated = crawler.outdated()))
                crawler.download();
            if (closeOnOld && !updated) {
                this.file = null;
                this.hash = null;
                return;
            }
            file = new File(crawler.pack);
        }
        this.file = file;
        this.archive = new JarArchive(file);
        this.classnodes = archive.build();
        this.visitors = visitors;
        this.hash = getHash();
    }

    public abstract String getType();

    public abstract String getHash();

    public abstract String getAccessorPrefix();

    public abstract String getWrapperPrefix();

    public abstract int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs);

    public String getAccessorPackage() {
        String prefix = getAccessorPrefix();
        return prefix.isEmpty() ? "" : prefix.substring(0, prefix.lastIndexOf('/'));
    }

    public String getAccessorParent() {
        String prefix = getAccessorPackage();
        return prefix.isEmpty() ? "" : prefix.substring(0, prefix.lastIndexOf('/'));
    }

    public abstract String getModscriptLocation();

    public void appendLine(String line) {
        builder.append(line).append("\n");
    }

    public GraphVisitor visitor(String visitor) {
        for (GraphVisitor gv : visitors) {
            if (gv.id().equals(visitor)) {
                return gv;
            }
        }
        return null;
    }

    public Map<ClassNode, Map<MethodNode, FlowGraph>> graphs() {
        return graphs;
    }

    @Override
    public void run() {
        if (file == null)
            return;
        List<ClassNode> remove = classnodes.values().stream().filter(cn -> cn.name.contains("/")).collect(Collectors.toList());
        for (ClassNode cn : remove)
            classnodes.remove(cn.name);
        Map<String, ClassFactory> factories = new HashMap<>();
        for (ClassNode cn : classnodes.values())
            factories.put(cn.name, new ClassFactory(cn));
        UnusedMethodTransform umt = new UnusedMethodTransform() {
            public void populateEntryPoints(List<ClassMethod> entries) {
                for (ClassFactory factory : factories.values()) {
                    entries.addAll(factory.findMethods(cm ->
                            cm.method.name.length() > (Updater.this instanceof RS3Updater ? 3 : 2)));
                    entries.addAll(factory.findMethods(cm -> {
                        String superName = factory.node.superName;
                        return factories.containsKey(superName) && factories.get(superName).findMethod(icm ->
                                icm.method.name.equals(cm.method.name) && icm.method.desc.equals(cm.method.desc)) != null;
                    }));
                    entries.addAll(factory.findMethods(cm -> {
                        for (String iface : factory.node.interfaces) {
                            if (factories.containsKey(iface)) {
                                ClassFactory impl = factories.get(iface);
                                if (impl.findMethod(icm -> icm.method.name.equals(cm.method.name) &&
                                        icm.method.desc.equals(cm.method.desc)) != null)
                                    return true;
                            }
                        }
                        return false;
                    }));
                }
            }
        };
        umt.transform(classnodes);
        for (GraphVisitor gv : visitors)
            gv.updater = this;
        for (GraphVisitor gv : visitors) {
            for (ClassNode cn : classnodes.values()) {
                if (gv.validate(cn)) {
                    gv.cn = cn;
                    break;
                }
            }
        }
        long graphTime = 0;
        long treeTime = 0;
        int trees = 0;
        long multTime = 0;
        long predTime = 0;
        this.inverseVisitor = new InverseVisitor(factories);
        for (ClassFactory cf : factories.values()) {
            for (ClassMethod cm : cf.methods) {
                long start = System.nanoTime();
                cm.method.accept(inverseVisitor);
                long end = System.nanoTime();
                multTime += (end - start);
            }
        }
        OpaquePredicateVisitor opv = new OpaquePredicateVisitor();
        for (ClassNode cn : classnodes.values()) {
            Map<MethodNode, FlowGraph> local = new HashMap<>();
            for (MethodNode mn : cn.methods) {
                long start = System.nanoTime();
                FlowGraph graph = new FlowGraph(mn);
                FlowVisitor visitor = new FlowVisitor();
                visitor.accept(mn);
                long end = System.nanoTime();
                graphTime += (end - start);
                start = System.nanoTime();
                opv.accept(mn);
                end = System.nanoTime();
                predTime += (end - start);
                graph.graph(visitor.graph);
                for (Block block : graph) {
                    start = System.nanoTime();
                    block.tree();
                    trees++;
                    end = System.nanoTime();
                    treeTime += (end - start);
//                    start = System.nanoTime();
//                    iv.visit(block);
//                    end = System.nanoTime();
//                    multTime += (end - start);
                }
                local.put(mn, graph);
            }
            graphs.put(cn, local);
        }
        revision = getRevision(graphs);
        appendLine("\tBuild #" + revision);
        appendLine("");
        int totalGraphs = 0;
        for (Map<MethodNode, FlowGraph> map : graphs.values()) {
            totalGraphs += map.size();
        }
        int totalClasses = 0;
        int classes = 0;
        int totalHooks = 0;
        int hooks = 0;
        int methodHooks = 0;
        Set<GraphVisitor> visitors = new TreeSet<>(new Comparator<GraphVisitor>() {
            public int compare(GraphVisitor g1, GraphVisitor g2) {
                return g1.id().compareTo(g2.id());
            }
        });
        Collections.addAll(visitors, this.visitors);
        long start = System.nanoTime();
        for (GraphVisitor visitor : this.visitors) {
            if (visitor.cn != null)
                visitor.visit();
        }
        long end = System.nanoTime();
        for (GraphVisitor gv : visitors) {
            totalClasses++;
            if (gv.cn == null) {
                appendLine("\t" + gv.id() + " is BROKEN");
                appendLine("");
                continue;
            }
            if (print) {
                appendLine("\t^ " + gv.cn.name + " implements " + gv.id());
            }
            if (gv.cn == null) {
                appendLine("");
                continue;
            }
            classes++;
            hooks += gv.hooks.size();
            for (Hook hook : gv.hooks.values()) {
                if (hook instanceof InvokeHook) {
                    methodHooks++;
                }
            }
            VisitorInfo info = gv.getClass().getAnnotation(VisitorInfo.class);
            if (info == null) {
                appendLine("");
                continue;
            }
            totalHooks += info.hooks().length;
            for (Hook hook : gv.hooks.values()) {
                if (hook instanceof FieldHook) {
                    FieldHook fh = (FieldHook) hook;
                    if (fh.fieldDesc.equals("I")) {
                        if (fh.multiplier == 0) {
                            BigInteger bigInt = inverseVisitor.inverseFor(fh.clazz, fh.field);
                            if (bigInt != null) {
                                fh.multiplier = bigInt.intValue();
                            }
                        }
                    }
                    if (!fh.isStatic) {
                        fh.clazz = gv.cn.name;
                    }
                } else if (hook instanceof InvokeHook) {
                    InvokeHook ih = (InvokeHook) hook;
                    OpaquePredicateVisitor.OpaquePredicate predicate = opv.get(ih.clazz + "." + ih.method + ih.desc);
                    if (predicate != null) {
                        ih.setOpaquePredicate(predicate.predicate, predicate.predicateType);
                    }
                }
                if (print) {
                    appendLine("\t" + hook.getOutput());
                }
            }
            for (String hook : info.hooks()) {
                if (!gv.hooks.containsKey(hook)) {
                    appendLine("\t!!! broken: " + gv.id() + "#" + hook);
                    //gv.hooks.put(hook, new BrokenHook(hook));
                }
            }
            appendLine("");
            gv.hooks.keySet().stream().filter(hook -> !Arrays.asList(info.hooks()).contains(hook)).forEach(hook ->
                            System.out.println("not in VisitorInfo annotation --> " + hook)
            );
        }
        this.classes = classes + "/" + totalClasses;
        this.hooks = hooks + "/" + totalHooks;
        appendLine(String.format("\tunused methods %d/%d", umt.getRemoved(), umt.getTotal()));
        /*appendLine(String.format("graphs --> %d in %.2f seconds", totalGraphs, graphTime / 1e9));
        appendLine(String.format("trees --> %d in %.2f seconds", trees, treeTime / 1e9));
        appendLine(String.format("multipliers --> %s in %.2f seconds", inverseVisitor.toString(), multTime / 1e9));
        appendLine(String.format("predicates --> %s in %.2f seconds", opv.toString(), predTime / 1e9));*/
        appendLine(String.format("\tIdentified %d/%d classes", classes, totalClasses));
        appendLine(String.format("\tIdentfiied %d/%d hooks, %d of which were methods", hooks, totalHooks, methodHooks));
        appendLine(String.format("\ttotal time %.2f seconds", (end - start) / 1e9));
        List<GraphVisitor> graphVisitors = new ArrayList<>();
        Collections.addAll(graphVisitors, this.visitors);
        try {
            appendLine("\thash " + hash);
            System.out.println(builder);
            String loc = getModscriptLocation();
            if (loc != null) {
                ModScript.write(loc, hash, getType(), graphVisitors);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void flush() {
        archive = null;
        for (Map.Entry<ClassNode, Map<MethodNode, FlowGraph>> entry : graphs.entrySet()) {
            for (Map.Entry<MethodNode, FlowGraph> mEntry : entry.getValue().entrySet()) {
                FlowGraph graph = mEntry.getValue();
                graph.flush();
            }
        }
        graphs.clear();
        graphs = null;
        visitors = null;
    }

    public void refactor(File target) throws IOException {
        for (GraphVisitor visitor : visitors) {
            if (visitor.cn == null) continue;
            for (Hook hook : visitor.hooks.values()) {
                if (hook instanceof FieldHook) {
                    FieldHook fh = (FieldHook) hook;
                    FieldNode fn = classnodes.get(fh.clazz).getField(fh.field, null, false);
                    if (fn == null) continue;
                    String newName = fh.name + "";
                    if (newName.length() == 1)
                        newName += "Value";
                    Assembly.rename(classnodes.values(), fn, newName);
                }
            }
        }
        for (GraphVisitor visitor : visitors) {
            if (visitor.cn == null) continue;
            Assembly.rename(classnodes.values(), visitor.cn, visitor.id());
        }
        archive.write(target);
    }
}
