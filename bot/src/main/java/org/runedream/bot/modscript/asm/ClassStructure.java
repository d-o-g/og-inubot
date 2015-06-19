/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.bot.modscript.asm;

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;
import org.runedream.api.util.filter.Filter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author unsigned
 * @since 01-05-2015
 */
public class ClassStructure extends ClassNode {

    public final List<ClassStructure> supers = new ArrayList<>();
    public final List<ClassStructure> delegates = new ArrayList<>();

    private final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    private byte[] bytes;

    public ClassStructure() {
        super(Opcodes.ASM5);
    }

    public static ClassStructure create(final byte[] bytes) {
        final ClassReader reader = new ClassReader(bytes);
        final ClassStructure node = new ClassStructure();
        reader.accept(node, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        return node;
    }

    public static ClassStructure create(final InputStream in) {
        try {
            final ClassReader cr = new ClassReader(in);
            final ClassStructure cs = new ClassStructure();
            cr.accept(cs, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
            return cs;
        } catch (IOException e) {
            return null;
        }
    }

    public byte[] getBytes(final boolean cached) {
        if (cached && bytes != null)
            return bytes;
        accept(writer);
        return (bytes = writer.toByteArray());
    }

    public byte[] getBytes() {
        return getBytes(false);
    }

    @Override
    public String toString() {
        return name;
        /*final StringBuilder sb = new StringBuilder(name).append(" extends ").append(superName);
        if (interfaces.size() == 0)
            return sb.toString();
        sb.append(" implements ").append(interfaces.get(0));
        for (final String iface : interfaces.subList(1, interfaces.size())) {
            sb.append(", ").append(iface);
        }
        return sb.toString();*/
    }

    public MethodNode getMethodFromSuper(final String name, final String desc) {
        for (final ClassStructure super_ : supers) {
            for (final MethodNode mn : super_.methods) {
                if (mn.name.equals(name) && mn.desc.equals(desc)) {
                    return mn;
                }
            }
        }
        return null;
    }

    public boolean isInherited(final String name, final String desc) {
        return getMethodFromSuper(name, desc) != null;
    }

    public boolean isInherited(final String methodOwner, final MethodNode mn) {
        return methodOwner.equals(name) && isInherited(mn.name, mn.desc);
    }

    public MethodNode getMethod(final String methodOwner, final String name, final String desc) {
        for (final MethodNode mn : methods) {
            if ((methodOwner + "." + name + desc).equals(this.name + "." + name + desc)) {
                return mn;
            }
        }
        return null;
    }

    public List<MethodNode> getMethods(final Filter<MethodNode> filter) {
        return super.methods.stream().filter(filter::accept).collect(Collectors.toList());
    }

    public List<FieldNode> getInstanceFields() {
        return super.fields.stream().filter(f -> !Modifier.isStatic(f.access)).collect(Collectors.toList());
    }

    public List<MethodNode> getInstanceMethods() {
        return super.methods.stream().filter(f -> !Modifier.isStatic(f.access)).collect(Collectors.toList());
    }

    public int getLevel() {
        return supers.size();
    }

    public ClassStructure getSuperType() {
        return supers.size() > 0 ? supers.get(0) : null; //direct superclass is always added first
    }
}

