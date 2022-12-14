package com.inubot.visitor;

import com.inubot.Updater;
import com.inubot.modscript.hook.*;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class GraphVisitor implements Opcodes {

    public final Map<String, Hook> hooks = new HashMap<>();

    public Updater updater;
    public ClassNode cn = null;
    public FlowGraph graph;

    private String id = null;

    public abstract boolean validate(ClassNode cn);

    public abstract void visit();

    public final String id() {
        return id != null ? id : (id = getClass().getSimpleName());
    }

    public String iface() {
        return updater.getAccessorPrefix() + id();
    }

    public String clazz(String visitor) {
        try {
            return updater.visitor(visitor).cn.name;
        } catch (NullPointerException e) {
            return "null";
        }
    }

    public ClassNode nodeFor(Class<? extends GraphVisitor> clazz) {
        String klass = clazz(clazz.getSimpleName());
        return klass != null ? updater.classnodes.get(klass) : null;
    }

    public String desc() {
        return desc(id());
    }

    public String desc(String visitor) {
        return "L" + clazz(visitor) + ";";
    }

    public String literalDesc() {
        return desc(id());
    }

    public String literalDesc(String visitor) {
        return "L" + updater.visitor(visitor).id() + ";";
    }

    public String key(String hook) {
        FieldHook fh = (FieldHook) hooks.get(hook);
        return fh != null ? fh.clazz + "." + fh.field : null;
    }

    public final void addHook(Hook hook) {
        if (hook.name == null)
            return;
        Hook hook0 = hooks.get(hook.name);
        if (hook0 != null && !(hook0 instanceof FieldHook) && !(hook0 instanceof InvokeHook)) {
            System.err.println("Hook overwrite --> " + id + "#" + hook.name);
        }
        hooks.put(hook.name, hook);
    }

    public final void add(String name, FieldNode fn) {
        if (name == null || fn == null)
            return;
        hooks.put(name, new FieldHook(name, fn));
    }

    public final void add(String name, FieldNode fn, String returnDesc) {
        if (name == null || fn == null)
            return;
        hooks.put(name, new FieldHook(name, fn));
    }

    public final void visit(String visitor, BlockVisitor bv) {
        ClassNode cn = updater.visitor(visitor).cn;
        if (cn == null)
            return;
        for (FlowGraph graph : updater.graphs().get(cn).values()) {
            this.graph = graph;
            for (Block block : graph) {
                if (bv.validate()) {
                    bv.visitInternal(block);
                }
            }
        }
    }

    public final void visit(BlockVisitor bv) {
        visit(id(), bv);
        bv.visitEnd();
    }

    public final void visitAll(BlockVisitor... bv) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (FlowGraph graph : map.values()) {
                this.graph = graph;
                for (Block block : graph) {
                    for (BlockVisitor b : bv) {
                        if (b.validate())
                            b.visitInternal(block);
                    }
                }
            }
        }
        for (BlockVisitor b : bv)
            b.visitEnd();
    }

    public final void visitIf(String visitor, BlockVisitor bv, Predicate<MethodNode> predicate) {
        ClassNode cn = updater.visitor(visitor).cn;
        if (cn == null)
            return;
        for (FlowGraph graph : updater.graphs().get(cn).values()) {
            if (!predicate.test(graph.method())) {
                continue;
            }
            this.graph = graph;
            for (Block block : graph) {
                if (bv.validate()) {
                    bv.visitInternal(block);
                }
            }
        }
    }

    public final void visitIf(BlockVisitor bv, Predicate<Block> blockPredicate) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (FlowGraph graph : map.values()) {
                this.graph = graph;
                for (Block block : graph) {
                    if (bv.validate() && blockPredicate.test(block))
                        bv.visitInternal(block);
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitIfM(BlockVisitor bv, Predicate<MethodNode> methodPredicate) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (Map.Entry<MethodNode, FlowGraph> graph : map.entrySet()) {
                if (!methodPredicate.test(graph.getKey()))
                    continue;
                this.graph = graph.getValue();
                for (Block block : this.graph) {
                    if (bv.validate())
                        bv.visitInternal(block);
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitLocalIfM(BlockVisitor bv, Predicate<MethodNode> methodPredicate) {
        for (Map.Entry<MethodNode, FlowGraph> graph : updater.graphs().get(cn).entrySet()) {
            if (!methodPredicate.test(graph.getKey()))
                continue;
            this.graph = graph.getValue();
            for (Block block : this.graph) {
                if (bv.validate())
                    bv.visitInternal(block);
            }
        }
        bv.visitEnd();
    }

    public final void visit(MethodVisitor mv) {
        for (MethodNode mn : cn.methods)
            mn.accept(mv);
    }

    public final void visitAll(MethodVisitor mv) {
        for (ClassNode cn : updater.classnodes.values()) {
            for (MethodNode mn : cn.methods)
                mn.accept(mv);
        }
    }

    public String getHookKey(String hook) {
        Hook h = hooks.get(hook);
        if (h == null)
            return null;
        FieldHook fh = (FieldHook) h;
        return fh.clazz + "." + fh.field;
    }

    public FieldHook getFieldHook(String hook) {
        Hook h = hooks.get(hook);
        if (h != null && h instanceof FieldHook) {
            return (FieldHook) h;
        }
        return null;
    }

    public FieldHook resolve(String field) {
        for (Hook hook : hooks.values()) {
            if (hook instanceof FieldHook && ((FieldHook) hook).key().equals(field)) {
                return (FieldHook) hook;
            }
        }
        return null;
    }
}