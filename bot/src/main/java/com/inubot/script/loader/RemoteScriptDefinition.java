/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.loader;

import com.inubot.bot.util.Configuration;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RemoteScriptDefinition extends ScriptDefinition {

    private final String name, developer, desc;
    private final double version;

    public RemoteScriptDefinition(String name, String developer, String desc, double version) {
        this.name = name;
        this.developer = developer;
        this.desc = desc;
        this.version = version;
    }

    public static RemoteScriptDefinition create(byte[] data) {
        try {
            FileOutputStream out = new FileOutputStream(Configuration.SCRIPTS + "ok.jar");
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
