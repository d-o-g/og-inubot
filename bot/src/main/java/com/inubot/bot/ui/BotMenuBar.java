/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import com.inubot.Inubot;
import com.inubot.api.methods.Client;

import javax.swing.*;
import java.awt.*;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class BotMenuBar extends JMenuBar {

    private final JMenuItem widget = new JMenuItem("Widget Explorer");
    private final JMenuItem acc = new JMenuItem("Account AccountCreator");

    private final JButton pause = new JButton("Pause");
    private final JButton start = new JButton("Start");
    private final JButton stop = new JButton("Stop");

    private final JMenu debug = new JMenu("Debug");
    private final JMenu tools = new JMenu("Tools");

    private JScrollPane scroller;

    public BotMenuBar() {
//        widget.addActionListener(e -> new WidgetExplorer().setVisible(true));
//        acc.addActionListener(e -> {
//            Account a = AccountManager.generateRandomAccount();
//            if (a != null) {
//                a.enterCredentials();
//            }
//        });

        scroller = new JScrollPane();
        scroller.setPreferredSize(new Dimension(0, 100));

        start.setEnabled(true);
        start.addActionListener(e -> new ScriptSelector().setVisible(true));

        pause.setEnabled(false);
        pause.addActionListener(e -> {
            Inubot.getInstance().getScriptFlux().switchState();
            updateButtonStates();
        });
        pause.setEnabled(false);

        stop.setEnabled(false);
        stop.addActionListener(e -> {
            Inubot.getInstance().getScriptFlux().stop();
            updateButtonStates();
        });
        stop.setEnabled(false);

        NexusToggleableButton farm = new NexusToggleableButton("Low CPU", () -> !Client.LANDSCAPE_RENDERING_ENABLED);
        farm.addActionListener(e -> {
            Client.LANDSCAPE_RENDERING_ENABLED = !Client.LANDSCAPE_RENDERING_ENABLED;
            Client.MODEL_RENDERING_ENABLED = !Client.MODEL_RENDERING_ENABLED;
            Client.WIDGET_RENDERING_ENABLED = !Client.WIDGET_RENDERING_ENABLED;
            Client.GAME_TICK_SLEEP = Client.LANDSCAPE_RENDERING_ENABLED ? -1 : 125;
            Client.setLowMemory(!Client.isLowMemory());
        });
        NexusToggleableButton log = new NexusToggleableButton("Console", () -> Inubot.getInstance().getLogPane().isToggled());
        log.addActionListener(e -> {
            if (!Inubot.getInstance().getLogPane().isToggled()) {
                scroller.add(Inubot.getInstance().getLogPane());
                Inubot.getInstance().add(scroller, BorderLayout.SOUTH);
            } else {
                scroller.remove(Inubot.getInstance().getLogPane());
                Inubot.getInstance().remove(scroller);
            }
            Inubot.getInstance().pack();
            Inubot.getInstance().getLogPane().setToggled(!Inubot.getInstance().getLogPane().isToggled());
        });

        add(start);
        add(pause);
        add(stop);

        add(Box.createHorizontalGlue());
        add(log);
        add(farm);
    }

    public void updateButtonStates() {
        start.setEnabled(!Inubot.getInstance().getScriptFlux().isRunning());
        stop.setEnabled(Inubot.getInstance().getScriptFlux().isRunning());
        pause.setEnabled(Inubot.getInstance().getScriptFlux().isRunning());
        pause.setText(Inubot.getInstance().getScriptFlux().isPaused() ? "Resume" : "Pause");
    }
}
