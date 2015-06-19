/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */

import com.inubot.bot.util.Configuration;
import com.inubot.script.loader.LocalScriptLoader;

import java.io.File;
import java.io.IOException;

/**
 * @author unsigned
 * @since 19-06-2015
 */
public class ScriptSelectorTest {

    public static void main(String... args) {
        LocalScriptLoader loader = new LocalScriptLoader();
        try {
            loader.parse(new File(Configuration.SCRIPTS));
            Class<?>[] definitions = loader.getMainClasses();
            for (Class<?> def : definitions)
                System.out.println(def.getClass().getSimpleName());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
