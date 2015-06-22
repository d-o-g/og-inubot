/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import com.inubot.Inubot;
import com.inubot.bot.util.Configuration;
import com.inubot.script.Script;
import com.inubot.script.loader.LocalScriptLoader;

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

        JScrollPane scroll = new JScrollPane(scripts);
        scroll.setPreferredSize(new Dimension(450, 110));

        scripts.setLayout(new GridLayout(3, 3, 5, 5));
        for (Class clazz : Inubot.SCRIPT_CLASSES)
            scripts.add(new Entity(clazz));
        LocalScriptLoader loader = new LocalScriptLoader();
        try {
            loader.parse(new File(Configuration.SCRIPTS));
            Class<?>[] definitions = loader.getMainClasses();
            for (Class def : definitions)
                scripts.add(new Entity(def));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        super.add(scroll);

        super.pack();
        super.setResizable(false);
    }

    private class Entity extends JPanel {

        public Entity(Class<? extends Script> target) {
            super.setLayout(new BorderLayout());
            super.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            JLabel name = new JLabel(target.getSimpleName());
            super.add(name, BorderLayout.CENTER);

            JButton start = new JButton("Start");
            start.addActionListener(e -> {
                try {
                    Script targetInstance = target.newInstance();
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
