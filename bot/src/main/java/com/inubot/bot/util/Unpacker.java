/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.*;
import java.util.zip.GZIPInputStream;

public class Unpacker {

    private static int[] chars;

    static {
        chars = new int[128];
        chars[42] = 62;
        chars[43] = 62;
        chars[45] = 63;
        chars[47] = 63;
        for (byte i = 48; i < 58; ++i) chars[i] = i + 4;
        for (byte i = 65; i < 91; ++i) chars[i] = i - 65;
        for (byte i = 97; i < 123; ++i) chars[i] = i - 71;
    }

    public final File pack;
    public final String keySpec, paramSpec;

    public Unpacker(File pack, String keySpec, String paramSpec) {
        this.pack = pack;
        this.keySpec = keySpec;
        this.paramSpec = paramSpec;
    }

    private static byte[] binary(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bit;
                while ((bit = fis.read(buffer)) != -1) {
                    out.write(buffer, 0, bit);
                }
                return out.toByteArray();
            }
        } catch (IOException e) {
            return null;
        }
    }

    public Map<String, byte[]> dump(File target) {
        byte[] bytes = binary(pack);
        if (bytes == null)
            return new HashMap<>();
        try (JarInputStream jis = new JarInputStream(new ByteArrayInputStream(bytes))) {
            Manifest manifest = jis.getManifest();
            if (manifest == null)
                throw new RuntimeException("Jar Manifest is null");
            Map<String, byte[]> innards = new HashMap<>();
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.getName().equals("inner.pack.gz") || entry.getName().equals("Rs2Applet.class"))
                    innards.put(entry.getName(), read(jis));
            }
            if (innards.containsKey("inner.pack.gz")) {
                SecretKeySpec key = new SecretKeySpec(toByte(keySpec), "AES");
                IvParameterSpec param = new IvParameterSpec(toByte(paramSpec));
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(2, key, param);
                byte[] unscrambled = cipher.doFinal(innards.get("inner.pack.gz"));
                Pack200.Unpacker unpacker = Pack200.newUnpacker();
                ByteArrayOutputStream baos = new ByteArrayOutputStream(0x500000);
                JarOutputStream jos = new JarOutputStream(baos);
                GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(unscrambled));
                unpacker.unpack(gzip, jos);
                if (target.equals(pack))
                    pack.delete();
                /*try (JarInputStream in = new JarInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
                    try (JarOutputStream out = new JarOutputStream(new FileOutputStream(target), manifest)) {
                        while ((entry = in.getNextJarEntry()) != null) {
                            String file = entry.getName();
                            if (file.startsWith("META-INF"))
                                continue;
                            if (file.endsWith(".class")) {
                                out.putNextEntry(entry);
                                out.write(read(in));
                                out.closeEntry();
                            }
                        }
                        for (Map.Entry<String, byte[]> mapEntry : innards.entrySet()) {
                            if (mapEntry.getKey().equals("inner.pack.gz"))
                                continue;
                            out.putNextEntry(new JarEntry(mapEntry.getKey()));
                            out.write(mapEntry.getValue());
                            out.closeEntry();
                        }
                    }
                }*/
            }
            return innards;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private byte[] read(JarInputStream jis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int read;
        while (jis.available() > 0 && (read = jis.read(buffer, 0, buffer.length)) >= 0) {
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

    private byte[] toByte(String key) {
        int keyLength = key.length();
        if (keyLength == 0) {
            return new byte[0];
        } else {
            int unscrambledLength;
            int lengthMod = -4 & keyLength + 3;
            unscrambledLength = lengthMod / 4 * 3;
            if (keyLength <= lengthMod - 2 || charIndex(key.charAt(lengthMod - 2)) == -1) {
                unscrambledLength -= 2;
            } else if (keyLength <= lengthMod - 1 || -1 == charIndex(key.charAt(lengthMod - 1))) {
                --unscrambledLength;
            }
            byte[] keyBytes = new byte[unscrambledLength];
            unscramble(keyBytes, 0, key);
            return keyBytes;
        }
    }

    private int charIndex(char character) {
        return character >= 0 && character < chars.length ? chars[character] : -1;
    }

    private int unscramble(byte[] bytes, int offset, String key) {
        int initialOffset = offset;
        int keyLength = key.length();
        int pos = 0;
        while (keyLength > pos) {
            int currentChar = charIndex(key.charAt(pos));
            int pos_1 = keyLength > (pos + 1) ? charIndex(key.charAt(pos + 1)) : -1;
            int pos_2 = pos + 2 < keyLength ? charIndex(key.charAt(2 + pos)) : -1;
            int pos_3 = keyLength > (pos + 3) ? charIndex(key.charAt(3 + pos)) : -1;
            bytes[offset++] = (byte) (pos_1 >>> 4 | currentChar << 2);
            if (pos_2 != -1) {
                bytes[offset++] = (byte) (pos_1 << 4 & 240 | pos_2 >>> 2);
                if (pos_3 != -1) {
                    bytes[offset++] = (byte) (192 & pos_2 << 6 | pos_3);
                    pos += 4;
                    continue;
                }
            }
            break;
        }
        return offset - initialOffset;
    }
}

