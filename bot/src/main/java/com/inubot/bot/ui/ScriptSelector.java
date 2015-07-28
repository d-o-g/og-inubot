/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import com.inubot.Bot;
import com.inubot.bot.util.CachedClassLoader;
import com.inubot.bot.util.Configuration;
import com.inubot.bundledscripts.complete.Combot;
import com.inubot.bundledscripts.complete.WineMaker;
import com.inubot.bundledscripts.complete.agility.PerfectAgility;
import com.inubot.bundledscripts.complete.alcher.ProAlcher;
import com.inubot.bundledscripts.complete.chopper.ProChopper;
import com.inubot.bundledscripts.complete.fisher.ProFisher;
import com.inubot.bundledscripts.complete.hunter.*;
import com.inubot.bundledscripts.complete.miner.ProMiner;
import com.inubot.bundledscripts.complete.rangeguild.RangeGuild;
import com.inubot.script.Manifest;
import com.inubot.script.Script;
import com.inubot.script.loader.LocalScriptLoader;
import com.inubot.script.loader.RemoteScriptDefinition;
import com.inubot.script.loader.ScriptDefinition;
import com.inubot.script.loader.ScriptFilter;
import me.mad.MadTutorial;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public class ScriptSelector extends JFrame {

    private static final Class[] SCRIPT_CLASSES = {

    };

    public ScriptSelector() {
        super("Script Selector");
        super.setLayout(new BorderLayout());
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel scripts = new JPanel();

        JScrollPane scroll = new JScrollPane(scripts, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(450, 150));

        LocalScriptLoader loader = new LocalScriptLoader();
        java.util.List<Entity> entities = new ArrayList<>();
        try {
            loader.parse(new File(Configuration.SCRIPTS));
            ScriptDefinition[] definitions = loader.getDefinitions();
            for (ScriptDefinition def : definitions) {
                entities.add(new Entity(def));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        CachedClassLoader remoteLoader = new CachedClassLoader(RemoteScriptDefinition.getNetworkedScriptDefinitions());
        ScriptFilter filter = new ScriptFilter();
        for (String name : RemoteScriptDefinition.getNetworkedScriptDefinitions().keySet()) {
            try {
                Class<?> clazz = remoteLoader.loadClass(name);
                if (filter.accept(clazz)) {
                    ScriptDefinition def = new ScriptDefinition(clazz.getAnnotation(Manifest.class));
                    def = new RemoteScriptDefinition(def.name(), def.developer(), def.desc(), def.version());
                    def.setScriptClass((Class<? extends Script>) clazz);
                    entities.add(new Entity(def));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Class<?> clazz : SCRIPT_CLASSES) {
            if (filter.accept(clazz)) {
                ScriptDefinition def = new ScriptDefinition(clazz.getAnnotation(Manifest.class));
                def = new RemoteScriptDefinition(def.name(), def.developer(), def.desc(), def.version());
                def.setScriptClass((Class<? extends Script>) clazz);
                entities.add(new Entity(def));
            }
        }

        int x = entities.size() / 3;
        int y = entities.size() / 3;
        if (x == 0)
            x = 3;
        if (y == 0)
            y = 3;
        scripts.setLayout(new GridLayout(x, x, y, y));
        outer:
        for (Entity entity : entities) {
            for (Component c : scripts.getComponents()) {
                if (c instanceof Entity) {
                    Entity entity0 = (Entity) c;
                    if (entity.target.getScriptClass() == entity0.target.getScriptClass()) {
                        continue outer;
                    }
                }
            }
            scripts.add(entity);
        }

        super.add(scroll);
        super.setLocationRelativeTo(null);
        super.pack();
        super.setResizable(false);
    }

    private class Entity extends JPanel {

        private final ScriptDefinition target;

        public Entity(ScriptDefinition target) {
            this.target = target;
            super.setLayout(new BorderLayout());
            super.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            if (target instanceof RemoteScriptDefinition) {
                setBackground(getBackground().darker());
            }

            JLabel name = new JLabel(target.name());
            name.setToolTipText(String.valueOf(target.version()));
            super.add(name, BorderLayout.CENTER);

            JButton start = new JButton("Start");
            start.setToolTipText(target.desc() + " - by " + target.developer());
            start.addActionListener(e -> {
                try {
                    Script targetInstance = target.getScriptClass().newInstance();
                    Bot.getInstance().getScriptFlux().execute(targetInstance);
                    dispose();
                } catch (InstantiationException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            });
            start.setFocusable(false);
            super.add(start, BorderLayout.SOUTH);
        }
    }
}
