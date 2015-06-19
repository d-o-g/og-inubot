/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.modscript.transform.util;

import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author unsigned
 * @since 25-04-2015
 */
public class MethodIterator {

    private final Collection<ClassNode> classes;
    public ClassNode currentOwner;

    public MethodIterator(Collection<ClassNode> classes) {
        this.classes = classes;
    }

    public void accept(Predicate<MethodNode> match, Consumer<MethodNode> consumer) {
        for (ClassNode cn : classes) {
            for (MethodNode mn : cn.methods) {
                if (!match.test(mn))
                    continue;
                currentOwner = cn;
                consumer.accept(mn);
            }
        }
    }
}
