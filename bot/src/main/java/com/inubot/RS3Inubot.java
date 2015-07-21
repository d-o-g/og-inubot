/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot;

import com.inubot.bot.modscript.Injector;
import com.inubot.bot.modscript.transform.*;
import com.inubot.bot.util.Unpacker;
import com.inubot.bot.util.io.Crawler;
import com.inubot.bot.util.io.Crawler.GameType;
import com.inubot.bot.util.io.JarNode;
import com.inubot.client.natives.modern.RSClient;

import javax.swing.*;
import java.io.File;
import java.util.Collections;

public class RS3Inubot extends Bot<RSClient> {

    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                instance = new RS3Inubot();
                instance.initArgs(args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static RS3Inubot getInstance() {
        return (RS3Inubot) instance;
    }

    @Override
    protected Injector initInjector(File pack) {
        Unpacker unpacker = new Unpacker(pack, crawler.parameters.get("0"), crawler.parameters.get("-1"));
        Injector injector = new Injector(new JarNode(unpacker.dump(pack)));
        Collections.addAll(injector.getTransforms(),
                new InterfaceImpl(),
                new CanvasHack(),
                new GetterAdder(),
                new CatchBlockSweeper()
        );
        return injector;
    }

    @Override
    protected void initCache(RSClient client) {
        //TODO def loader for rs3 after acquiring more hooks
    }

    @Override
    protected Crawler createCrawler() {
        return new Crawler(GameType.RS3);
    }
}
