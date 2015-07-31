/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber;

import org.objectweb.asm.commons.cfg.transform.UnusedMethodTransform;
import org.objectweb.asm.commons.wrapper.ClassFactory;
import org.objectweb.asm.commons.wrapper.ClassMethod;

import java.util.List;
import java.util.Map;

/**
 * @author Dogerina
 * @since 31-07-2015
 */
public class DummyMethodVisitor extends UnusedMethodTransform {

    private final Map<String, ClassFactory> factories;

    public DummyMethodVisitor(Map<String, ClassFactory> factories) {
        this.factories = factories;
    }

    @Override
    public void populateEntryPoints(List<ClassMethod> entries) {
        for (ClassFactory factory : factories.values()) {
            entries.addAll(factory.findMethods(cm ->
                    cm.method.name.length() > 2));
            entries.addAll(factory.findMethods(cm -> {
                String superName = factory.node.superName;
                while (superName != null) {
                    if (factories.containsKey(superName) && factories.get(superName).findMethod(icm ->
                            icm.method.name.equals(cm.method.name) && icm.method.desc.equals(cm.method.desc)) != null) {
                        return true;
                    }
                    ClassFactory super_ = factories.get(superName);
                    superName = super_ != null ? super_.superName() : null;
                }
                return false;
            }));
            entries.addAll(factory.findMethods(cm -> {
                for (String iface : factory.node.interfaces) {
                    if (factories.containsKey(iface)) {
                        ClassFactory impl = factories.get(iface);
                        while (impl != null) {
                            if (impl.findMethod(icm -> icm.method.name.equals(cm.method.name) &&
                                    icm.method.desc.equals(cm.method.desc)) != null) {
                                return true;
                            }
                            impl = factories.get(impl.superName());
                        }
                    }
                }
                return false;
            }));
        }
    }
}
