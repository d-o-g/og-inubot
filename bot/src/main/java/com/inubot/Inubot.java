/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot;

import com.inubot.api.util.CacheLoader;
import com.inubot.bot.modscript.Injector;
import com.inubot.bot.modscript.transform.*;
import com.inubot.bot.util.io.Crawler;
import com.inubot.bot.util.io.Crawler.GameType;
import com.inubot.client.natives.oldschool.RSClient;
import com.inubot.script.bundled.BlueDragonKiller;
import com.inubot.script.bundled.agility.PerfectAgility;
import com.inubot.script.bundled.fisher.AutoFisherPRO;
import com.inubot.script.bundled.hunter.*;
import com.inubot.script.bundled.rangeguild.RangeGuild;
import com.inubot.script.others.PestControl;
import com.inubot.script.others.septron.Combot;
import com.inubot.script.others.septron.fletching.Stringer;

import javax.swing.*;
import java.util.Collections;

public class Inubot extends Bot<RSClient> {

    public static final Class[] SCRIPT_CLASSES = new Class[]{
            AutoFisherPRO.class,
            Combot.class,
            RedChinsPRO.class,
            FalconryPRO.class,
            BirdSnarePRO.class,
            RangeGuild.class,
            PerfectAgility.class,
            PestControl.class,
            BlueDragonKiller.class,
            Stringer.class
    };

    @Override
    protected void initInjector(Injector injector) {
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
                new UserDetailsSetter(),
                new VarpBitHack(),
                new LandscapeHack(),
                new LowMemorySetter(),
                new CatchBlockSweeper()
        );
    }

    @Override
    protected void initCache(RSClient client) {
        CacheLoader.load(client);
    }

    @Override
    protected Crawler createCrawler() {
        return new Crawler(GameType.OSRS);
    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            try {
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
}
