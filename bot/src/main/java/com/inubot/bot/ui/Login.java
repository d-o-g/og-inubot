/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import com.inubot.Inubot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {

    public static void main(String... args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame("Inubot");
        frame.setLayout(new BorderLayout());
        JPanel lblPanel = new JPanel();
        lblPanel.setLayout(new BoxLayout(lblPanel, BoxLayout.Y_AXIS));
        lblPanel.add(new JLabel("Username"));
        lblPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        lblPanel.add(new JLabel("Password"));
        lblPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        frame.getContentPane().add(lblPanel, BorderLayout.WEST);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        JTextField username = new JTextField();
        username.setPreferredSize(new Dimension(120, username.getHeight()));
        fieldPanel.add(username);
        JTextField password = new JTextField();
        password.setPreferredSize(new Dimension(120, password.getHeight()));
        fieldPanel.add(password);
        frame.getContentPane().add(fieldPanel, BorderLayout.EAST);

        JButton login = new JButton("Login");
        frame.getContentPane().add(login, BorderLayout.SOUTH);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Inubot.main(args);
                //just pass the text in username/password textfields to servercon?
            }
        });

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
