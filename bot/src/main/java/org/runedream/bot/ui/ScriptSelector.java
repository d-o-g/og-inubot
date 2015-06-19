/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.bot.ui;

import org.runedream.RuneDream;
import org.runedream.script.Script;

import javax.swing.*;
import java.awt.*;

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
        scripts.setLayout(new GridLayout(3, 3, 5, 5));
        for (Class clazz : RuneDream.SCRIPT_CLASSES)
            scripts.add(new Entity(clazz));
        super.add(scripts, BorderLayout.EAST);

        JPanel auths = new JPanel();
        auths.setLayout(new BorderLayout());
        auths.add(new JLabel("Auth codes"), BorderLayout.NORTH);
        JList<String> authList = new JList<>(new String[]{"Click to reveal"});
        authList.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        auths.add(authList, BorderLayout.CENTER);
        authList.setSize(authList.getWidth(), auths.getHeight() - authList.getHeight());
        super.add(auths, BorderLayout.WEST);

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
                    RuneDream.getInstance().getScriptFlux().execute(targetInstance);
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
