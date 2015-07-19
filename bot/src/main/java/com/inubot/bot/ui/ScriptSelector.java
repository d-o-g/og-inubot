/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import com.inubot.Inubot;
import com.inubot.bot.util.Configuration;
import com.inubot.script.Manifest;
import com.inubot.script.Script;
import com.inubot.script.loader.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public class ScriptSelector extends JFrame {

    public ScriptSelector() {
        super("Script Selector");
        super.setLayout(new BorderLayout());
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel scripts = new JPanel();

        JScrollPane scroll = new JScrollPane(scripts, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(450, 150));

        scripts.setLayout(new GridLayout(3, 3, 5, 5));
        for (Class clazz : Inubot.SCRIPT_CLASSES) {
            if (clazz.isAnnotationPresent(Manifest.class)) {
                Manifest m = (Manifest) clazz.getAnnotation(Manifest.class);
                scripts.add(new Entity(new ScriptDefinition(m)));
            } else {
                scripts.add(new Entity(new RemoteScriptDefinition(clazz.getSimpleName(), "Developer", "", 1.0)));
            }
        }
        LocalScriptLoader loader = new LocalScriptLoader();
        try {
            loader.parse(new File(Configuration.SCRIPTS));
            ScriptDefinition[] definitions = loader.getDefinitions();
            for (ScriptDefinition def : definitions) {
                scripts.add(new Entity(def));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        super.add(scroll);
        super.setLocationRelativeTo(null);
        super.pack();
        super.setResizable(false);
    }

    private class Entity extends JPanel {

        public Entity(ScriptDefinition target) {
            super.setLayout(new BorderLayout());
            super.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            JLabel name = new JLabel(target.name());
            name.setToolTipText(String.valueOf(target.version()));
            super.add(name, BorderLayout.CENTER);

            JButton start = new JButton("Start");
            start.setToolTipText(target.desc() + " - by " + target.developer());
            start.addActionListener(e -> {
                try {
                    Script targetInstance = target.getScriptClass().newInstance();
                    Inubot.getInstance().getScriptFlux().execute(targetInstance);
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
