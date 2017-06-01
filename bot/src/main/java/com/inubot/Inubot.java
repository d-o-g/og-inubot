/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot;

import com.inubot.api.methods.Login;
import com.inubot.api.util.CacheLoader;
import com.inubot.api.util.Time;
import com.inubot.bot.modscript.Injector;
import com.inubot.bot.modscript.transform.*;
import com.inubot.bot.util.io.Crawler;
import com.inubot.bot.util.io.Crawler.GameType;
import com.inubot.bot.util.io.JarNode;
import com.inubot.client.natives.oldschool.RSClient;

import javax.swing.*;
import java.io.File;
import java.util.Collections;

public class Inubot extends Bot<RSClient> {

    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (int i = 0; i < args.length; i++) {
                    String arg = args[i];
                    if (arg.equals("-login")) {
                        String username = args[i + 1];
                        String password = args[i + 2];
                        new Thread(() -> {
                            while (true) {
                                Time.sleep(100);
                                if (getInstance() == null || getInstance().getClient() == null)
                                    continue;
                                if (Login.getState() != Login.STATE_CREDENTIALS)
                                    continue;
                                break;
                            }
                            System.out.println("Setting username and password.");
                            Login.setUsername(username);
                            Login.setPassword(password);
                        }).start();

                    }
                }
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                instance = new Inubot();
                instance.initArgs(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static Inubot getInstance() {
        return (Inubot) instance;
    }

    @Override
    protected Injector initInjector(File pack) {
        Injector injector = new Injector(new JarNode(pack));
        Collections.addAll(injector.getTransforms(),
                new EngineTickCallback(),
                new ProcessActionCallback(),
                new ProcessActionInvoker(),
                new InterfaceImpl(),
                new ModelHack(),
                new CanvasHack(),
                new GetterAdder(),
                new InvokerTransform(),
                new IdleTimeSetter(),
                new HoveredRegionTileSetter(),
                new GroundItemPosition(),
                new MessageCallback(),
                new ExperienceCallback(),
                new UserDetailsSetter(),
                new WidgetHack(),
                new LandscapeHack(),
                new LowMemorySetter(),
                new StaticSetters(),
                new CatchBlockSweeper(),
                new GraphicsProducer()
        );
        return injector;
    }

    @Override
    protected void initCache(RSClient client) {
        CacheLoader.load(client);
    }

    @Override
    protected Crawler createCrawler() {
        return new Crawler(GameType.OSRS);
    }
}
