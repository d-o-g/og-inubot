/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */

import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.jar.*;

public final class RuneMateInjector implements Opcodes {

    private final String file;

    /**
     * @param file The RuneMate jar file path
     */
    private RuneMateInjector(String file) {
        this.file = file;
    }

    //Run the dumped jar after you run this with -noverify vm arg
    public static void main(String... args) {
        RuneMateInjector injector = new RuneMateInjector("./runemate.jar");
        try {
            injector.inject();
            //injector.loadJar(injector.inject());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * This method gets called by RuneMate
     */
    public static void fieldcallback(String var0, String var1, String var2, long var5) {
        StringBuilder sb = new StringBuilder("^ ").append(var0).append(" is ").append(var1).append(".").append(var2);
        if (var5 != 1) {
            sb.append(" * ").append(var5);
        }
        System.out.println(sb.toString());
    }

    /**
     * This method gets called by RuneMate
     */
    public static void methodcallback(String var0, String var1, String var2, String var3) {
        String desc0 = org.objectweb.asm.Type.getType(var3).getReturnType().getClassName();
        org.objectweb.asm.Type[] args = org.objectweb.asm.Type.getArgumentTypes(var3);
        String params = "(";
        int i = 0;
        for (org.objectweb.asm.Type arg : args) {
            String ok = arg.getClassName();
            if (ok.lastIndexOf('.') != -1) {
                ok = ok.substring(ok.lastIndexOf('.') + 1);
            }
            params += ok;
            if (++i != args.length) {
                params += ", ";
            }
        }
        params += ")";
        System.out.println("¤ " + desc0 + " " + var0 + " is " + var1 + "#" + var2 + params);
    }

    private void loadJar(Map<String, byte[]> classes) {
        //CachedClassLoader classLoader = new CachedClassLoader(classes);
        //try {
            //Class<?> entryClass = classLoader.loadClass("com.runemate.Boot");
            //entryClass.newInstance();
        //} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
          //  e.printStackTrace();
        //}
    }

    /**
     * Injects a callback into RuneMate's hook wrappers constructor
     *
     * @throws Throwable
     */
    private Map<String, byte[]> inject() throws Throwable {
        Map<String, ClassNode> classes = new HashMap<>();
        Map<String, byte[]> resources = new HashMap<>();
        JarFile jar = new JarFile(file);
        Manifest manifest = jar.getManifest();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(".class")) {
                ClassNode cn = new ClassNode();
                ClassReader cr = new ClassReader(jar.getInputStream(entry));
                cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                classes.put(name.replace(".class", ""), cn);
            } else if (!name.equals("META-INF/MANIFEST.MF")) {
                resources.put(name, inputToBytes(jar.getInputStream(entry)));
            }
        }
        jar.close();
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                if (mn.desc.equals("(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZJ)V") && mn.name.equals("<init>")) {
                    InsnList stack = new InsnList();
                    stack.add(new VarInsnNode(ALOAD, 1));
                    stack.add(new VarInsnNode(ALOAD, 2));
                    stack.add(new VarInsnNode(ALOAD, 3));
                    stack.add(new VarInsnNode(LLOAD, 6));
                    stack.add(new MethodInsnNode(INVOKESTATIC, "RuneMateInjector", "fieldcallback", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V", false));
                    mn.instructions.insert(stack);
                    System.out.println("Injected fieldhook callback at... " + cn.name + "." + mn.name + mn.desc);
                } else if (mn.desc.equals("(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/util/Collection;)V")) {
                    InsnList stack = new InsnList();
                    stack.add(new VarInsnNode(ALOAD, 1));
                    stack.add(new VarInsnNode(ALOAD, 2));
                    stack.add(new VarInsnNode(ALOAD, 3));
                    stack.add(new VarInsnNode(ALOAD, 4));
                    stack.add(new MethodInsnNode(INVOKESTATIC, "RuneMateInjector", "methodcallback", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false));
                    mn.instructions.insert(stack);
                    System.out.println("Injected methodhook callback at... " + cn.name + "." + mn.name + mn.desc);
                }
            }
        }
        Map<String, byte[]> profiled = new HashMap<>();
        profiled.putAll(resources);
        for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            entry.getValue().accept(writer);
            profiled.put(entry.getKey().replaceAll("\\.", "/") + ".class", writer.toByteArray());
        }
        try (JarOutputStream output = new JarOutputStream(new FileOutputStream("dumped.jar"), manifest)) {
            for (Map.Entry<String, ClassNode> entry : classes.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey().replaceAll("\\.", "/") + ".class"));
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                entry.getValue().accept(writer);
                output.write(writer.toByteArray());
                output.closeEntry();
            }
            for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey()));
                output.write(entry.getValue());
                output.closeEntry();
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profiled;
    }

    private byte[] inputToBytes(InputStream in) {
        try (ReadableByteChannel inChannel = Channels.newChannel(in)) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (WritableByteChannel outChannel = Channels.newChannel(baos)) {
                    ByteBuffer buffer = ByteBuffer.allocate(4096);
                    while (inChannel.read(buffer) != -1) {
                        buffer.flip();
                        outChannel.write(buffer);
                        buffer.compact();
                    }
                    buffer.flip();
                    while (buffer.hasRemaining())
                        outChannel.write(buffer);
                    return baos.toByteArray();
                }
            }
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
