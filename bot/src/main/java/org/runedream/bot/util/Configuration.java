package org.runedream.bot.util;

import org.runedream.RuneDream;

import java.io.File;
import java.io.IOException;

public class Configuration {

    public static final String APPLICATION_NAME = "ImpSoft - Nexus";
    public static final String HOME = getSystemHome() + File.separator + "runedream" + File.separator;
    public static final String CACHE = HOME + "cache" + File.separator;
    public static final String DATA = CACHE + "data" + File.separator;
    public static final String ACTIONS = DATA + "actionbar.dog";
    public static final String[] DIRECTORIES = {CACHE, DATA};
    public static int WORLD = 59;

    public static boolean isLocal() {
        return !RuneDream.class.getResource(RuneDream.class.getSimpleName() + ".class").toString().contains("jar:");
    }

    public static void setup() {
        for (String dir : DIRECTORIES)
            new File(dir).mkdirs();
        File actions = new File(ACTIONS);
        if (!actions.exists()) {
            try {
                actions.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getSystemHome() {
        return OS.get() == OS.WINDOWS ? System.getProperty("user.home") + "/Documents/" : System.getProperty("user.home") + "/";
    }
}
