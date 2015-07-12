/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.loader;

import com.inubot.script.Script;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

/**
 * @author unsigned
 * @since 19-06-2015
 */
public class LocalScriptLoader extends ScriptLoader<File> {

    private final List<Class<? extends Script>> classes;

    public LocalScriptLoader() {
        this.classes = new ArrayList<>();
    }

    @Override
    public void parse(File root) throws IOException, ClassNotFoundException {
        classes.clear();
        Stack<File> files = new Stack<>();
        URLClassLoader loader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
        files.push(root);
        if (root.isDirectory()) {
            for (File file : root.listFiles()) {
                files.push(file);
            }
        }
        while (!files.isEmpty()) {
            File file = files.pop();
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if (subFiles != null)
                    Collections.addAll(files, subFiles);
            } else if (file.getName().endsWith(".class")) {
                String raw = file.getPath();
                raw = raw.substring(root.getPath().length() + 1);
                raw = raw.substring(0, raw.length() - ".class".length());
                raw = raw.replace(File.separatorChar, '.');
                Class<?> c = loader.loadClass(raw);
                if (super.accept(c))
                    classes.add((Class<? extends Script>) c);
            } else if (file.getName().endsWith(".jar")) {
                JarFile jar = new JarFile(file);
                URLClassLoader ucl = new URLClassLoader(new URL[]{file.toURI().toURL()});
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String name = entry.getName();
                        name = name.substring(0, name.length() - ".class".length());
                        name = name.replace('/', '.');
                        Class<?> c = ucl.loadClass(name);
                        if (super.accept(c))
                            classes.add((Class<? extends Script>) c);
                    }
                }
            }
        }
    }

    @Override
    public Class<?>[] getMainClasses() {
        return classes.toArray(new Class[classes.size()]);
    }
}
