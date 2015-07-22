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
import com.inubot.bot.util.io.JarNode;
import com.inubot.client.natives.oldschool.RSClient;
import com.inubot.script.bundled.*;
import com.inubot.script.bundled.proscripts.agility.PerfectAgility;
import com.inubot.script.bundled.fisher.AutoFisherPRO;
import com.inubot.script.bundled.hunter.*;
import com.inubot.script.bundled.proscripts.alcher.ProAlcher;
import com.inubot.script.bundled.proscripts.miner.ProMiner;
import com.inubot.script.bundled.proscripts.zulrah.ProZulrah;
import com.inubot.script.bundled.rangeguild.RangeGuild;
import com.inubot.script.bundled.tutisland.TutorialIsland;
import com.inubot.script.others.PestControl;
import com.inubot.script.others.RealZoo;
import com.inubot.script.others.septron.Combot;
import com.inubot.script.others.septron.fletching.Fletcher;
import com.inubot.script.others.septron.fletching.Stringer;

import javax.swing.*;
import java.io.File;
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
            Fletcher.class,
            Stringer.class,
            TutorialIsland.class,
            Chinner.class,
            RealZoo.class,
            ZooFighter.class,
            ProMiner.class,
            ProAlcher.class,
            ProZulrah.class,
            MinowolfFighter.class
    };

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
                new VarpBitHack(),
                new LandscapeHack(),
                new LowMemorySetter(),
                new CatchBlockSweeper()
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
