/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot;

import java.io.IOException;

public class Bootstrap {

    private static final String flags = "-noverify -Xmx256m -Xss2m -Dsun.java2d.noddraw=true -XX:CompileThreshold=1500 -Xincgc -XX:+UseConcMarkSweepGC -XX:+UseParNewGC";

    private static String location() {
        return Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    private static boolean valid() {
        return Double.parseDouble(System.getProperty("java.version").substring(0, 3)) >= 1.8;
    }

    public static void main(String... arguments) throws IOException {
        if (!valid()) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        String system = System.getProperty("os.name");
        if (system.contains("Windows")) {
            builder.append("java " + flags);
        }
        if (system.contains("Linux")) {
            builder.append("java " + flags);
        }
        if (system.contains("Mac")) {
            builder.append("java " + flags);
        }
        builder.append(" -cp \"").append(location()).append("\" com.inubot.bot.ui.Login");
        System.out.println("Executing: " + builder.toString());
        if (System.getProperty("os.name").contains("Windows")) {
            Runtime.getRuntime().exec(builder.toString());
        } else {
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", builder.toString()});
        }
    }
}
