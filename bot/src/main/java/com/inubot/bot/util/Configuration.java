package com.inubot.bot.util;

import com.inubot.Inubot;

import java.io.File;

public class Configuration {

    public static final String APPLICATION_NAME = "Inubot";
    public static final String HOME = getSystemHome() + File.separator + "inubot" + File.separator;
    public static final String CACHE = HOME + "cache" + File.separator;
    public static final String DATA = CACHE + "data" + File.separator;
    public static final String SCRIPTS = HOME + "scripts" + File.separator;
    public static final String INJECTED = CACHE + "injected.jar";
    public static final String INJECT_CACHE = CACHE + "inject_cache.wyd";
    public static final String[] DIRECTORIES = {CACHE, DATA, SCRIPTS};
    public static int WORLD = 18;

    public static boolean isLocal() {
        return !Inubot.class.getResource(Inubot.class.getSimpleName() + ".class").toString().contains("jar:");
    }

    static {
        for (String dir : DIRECTORIES) {
            new File(dir).mkdirs();
        }
    }

    public static String getSystemHome() {
        return OS.get() == OS.WINDOWS ? System.getProperty("user.home") + "/Documents/" : System.getProperty("user.home") + "/";
    }
}
