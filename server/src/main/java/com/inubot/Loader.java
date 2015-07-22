package com.inubot;

import com.inubot.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
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

	private byte[] load(File file) throws Exception {
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		byte[] data = new byte[dis.available()];
		dis.readFully(data);
		return data;
	}

	public void load() throws Exception {
		File root = new File(Loader.root);
		Stack<File> files = new Stack<>();
		File[] asd = root.listFiles();
		if (asd == null) return;
		for (File file : asd)
			files.push(file);
		loop:
		while (!files.isEmpty()) {
			File file = files.pop();
			if (file.getName().endsWith(".jar")) {
				JarFile jar = new JarFile(file);
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String name = entry.getName();
					if (name.endsWith(".class")) {
						name = name.replace("/", ".").replace(".class", "");
						URLClassLoader loader = URLClassLoader.newInstance(new URL[] {file.toURI().toURL()});
						Class<?> clazz = loader.loadClass(name);
						if (Script.class.isAssignableFrom(clazz)) {
							String store = name.substring(name.lastIndexOf(".") + 1).replace(".class", "");
							byte[] data = load(file);
							scripts.put(store, data);
							logger.info("Stored " + data.length + " for script " + store);
							continue loop;
						}
					}
				}
				jar.close();
			}
		}
	}
}
