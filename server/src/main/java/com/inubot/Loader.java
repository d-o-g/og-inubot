package com.inubot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Septron
 * @since July 12, 2015
 */
public class Loader {

    private static final Logger logger = LoggerFactory.getLogger(Loader.class);

    public static final Map<String, byte[]> scripts = new HashMap<>();

    public static final Loader singleton = new Loader();

    private static String root = "./data/scripts/";

    private Loader() {

    }

    public void load() throws Exception {
        File root = new File(Loader.root);
        Stack<File> files = new Stack<>();
        for (File file : root.listFiles())
            files.push(file);
        while (!files.isEmpty()) {
            File file = files.pop();
            if (file.getName().endsWith(".jar")) {
                InputStream stream = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(stream);
                byte[] data = new byte[dis.available()];
                dis.readFully(data);
                JarFile jar = new JarFile(file);
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.endsWith(".class")) {
                        name = name.substring(name.lastIndexOf("/") + 1).replace(".class", "");
                        //TODO: Make sure it's a script class -.-
                        scripts.put(name, data);
                    }
                }
                jar.close();
                dis.close();
            }
        }
    }
}
