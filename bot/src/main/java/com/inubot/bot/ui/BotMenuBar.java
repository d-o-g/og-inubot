/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class BotMenuBar extends JMenuBar {

    private final List<JMenu> menus = new ArrayList<JMenu>() {
        {
            super.add(new JMenu("Dev Tools"));
            super.add(new JMenu("Settings"));
            super.add(new JMenu("View Handler"));
        }
    };

    public BotMenuBar() {
        final JMenuItem widget = new JMenuItem("Widget Explorer");
        widget.addActionListener(e -> new WidgetExplorer().setVisible(true));
        this.menus.forEach(BotMenuBar.this::add);

        for (JMenu menu : menus) {
            if (menu.getText().equals("Dev Tools")) {
                add(widget);
            }
        }
    }
}
