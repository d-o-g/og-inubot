/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.loader;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RemoteScriptDefinition extends ScriptDefinition {

    private static final Map<String, byte[]> networkedScriptDefinitions = new HashMap<>();
    private final String name, developer, desc;
    private final double version;
    private byte[] buffer;

    public RemoteScriptDefinition(String name, String developer, String desc, double version) {
        this.name = name;
        this.developer = developer;
        this.desc = desc;
        this.version = version;
    }

    public static Map<String, byte[]> getNetworkedScriptDefinitions() {
        return networkedScriptDefinitions;
    }

    public static void addNetworkedDefinition(byte[] data) {
        Map<String, byte[]> map = create(data);
        networkedScriptDefinitions.putAll(map);
    }

    public static Map<String, byte[]> create(byte[] data) {
        Map<String, byte[]> classes = new HashMap<>();
        try (ZipInputStream input = new JarInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry;
            while ((entry = input.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    byte[] read = read(input);
                    entryName = entryName.replace('/', '.');
                    String name = entryName.substring(0, entryName.length() - 6);
                    classes.put(name, read);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static byte[] read(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int read;
        while (inputStream.available() > 0) {
            read = inputStream.read(buffer, 0, buffer.length);
            if (read < 0) {
                break;
            }
            out.write(buffer, 0, read);
        }
        return out.toByteArray();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String developer() {
        return developer;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public double version() {
        return version;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
