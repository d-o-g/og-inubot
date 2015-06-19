/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import javax.swing.*;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class BotMenuBar extends JMenuBar {

    private final JMenuItem widget = new JMenuItem("Widget Explorer");

    private final JMenu debug = new JMenu("Debug");

    public BotMenuBar() {
        widget.addActionListener(e -> new WidgetExplorer().setVisible(true));
        debug.add(widget);
        add(debug);
    }
}
