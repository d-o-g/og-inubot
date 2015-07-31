package com.renamer.util;

import com.renamer.tree.ClassMember;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.commons.Remapper;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * @author Mark
 *
 */
public class JarLoader {

	private File file;
	private Map<String, byte[]> data;
	private Map<String, ClassNode> classes;
	private Map<String, ClassMember> interfaces;
	public JarLoader(File file) {
		this.file = file;
	}

	public void load() throws IOException {
		Map<String, ClassMember> interfaces = new HashMap<String, ClassMember>();
		Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
		this.data = new HashMap<String, byte[]>();
		if (file.getName().endsWith(".jar")) {
			ZipFile zf = new ZipFile(file);
			Enumeration<?> e = zf.entries();
			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();
				if (ze.isDirectory()) {
					continue;
				}
				String name = ze.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.lastIndexOf(".class"));
					ClassReader cr = new ClassReader(zf.getInputStream(ze));
					ClassMember in = new ClassMember(false);
					ClassNode cn = new ClassNode();
					cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
					cn.accept(in);
					classes.put(name, cn);
					interfaces.put(name, in);
				} else {
					InputStream in = zf.getInputStream(ze);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					final int BUF_SIZE = 1 << 8;
					byte[] buffer = new byte[BUF_SIZE];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) >= 0) {
						out.write(buffer, 0, bytesRead);
					}
					in.close();
					data.put(ze.getName(), out.toByteArray());
				}
			}
			zf.close();
		}
		this.classes = classes;
		this.interfaces = interfaces;
	}

	public Map<String, ClassNode> getClassNodes() {
		return classes;
	}

	public Map<String, ClassMember> getInterfaceNodes() {
		return interfaces;
	}

	public void writeClasses(Remapper remapper) {
		interfaces = null;
		for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
			ClassWriter writer = new ClassWriter(0);
			entry.getValue().accept(writer);
			data.put(remapper.map(entry.getKey()) + ".class", writer.toByteArray());
		}
		classes = null;
	}

	public void dump(File f) throws IOException {
		FileOutputStream stream = new FileOutputStream(f);
		JarOutputStream out = new JarOutputStream(stream);
		for (String s : data.keySet()) {
			if(s.endsWith(".class")) {
				out.putNextEntry(new JarEntry(s));
				out.write(data.get(s));
			}
		}

		out.close();
		stream.close();
	}
}
