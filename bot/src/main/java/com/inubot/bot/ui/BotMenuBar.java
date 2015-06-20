/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import temp.account.Account;
import temp.account.AccountManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class BotMenuBar extends JMenuBar {

    private final JMenuItem widget = new JMenuItem("Widget Explorer");
    private final JMenuItem acc = new JMenuItem("Account Creator");

    private final JMenu debug = new JMenu("Debug");
    private final JMenu tools = new JMenu("Tools");

    public BotMenuBar() {
        widget.addActionListener(e -> new WidgetExplorer().setVisible(true));
        acc.addActionListener(e -> {
            Account a = AccountManager.generateRandomAccount();
            if (a != null) {
                a.enterCredentials();
            }
        });
        debug.add(widget);
        add(debug);

        tools.add(acc);
        add(tools);
    }
}
