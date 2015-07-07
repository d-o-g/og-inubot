package com.inubot.bot.ui;

import com.inubot.bot.account.Account;
import com.inubot.bot.account.AccountManager;
import com.inubot.bot.net.AccountCreator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Septron
 * @since June 22, 2015
 */
public class CreaterGUI extends JFrame {

    private JPasswordField password = new JPasswordField(30);
    private JTextField username = new JTextField(30);
    private JButton submit = new JButton("Submit");

    private JLabel a = new JLabel("Username:");
    private JLabel b = new JLabel("Password:");

    public CreaterGUI() {
        setTitle("Account AccountCreator");
        init();
        pack();
    }

    private void init() {
        GridBagConstraints constraints = new GridBagConstraints();
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        setLocationRelativeTo(null);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(a, constraints);

        constraints.gridx = 1;
        add(username, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(b, constraints);

        constraints.gridx = 1;
        add(password, constraints);

        submit.addActionListener(e -> new Thread(() -> {
            setVisible(false);
            dispose();
            String name = username.getText();
            String pass = String.valueOf(password.getPassword());
            String email = name.replace(" ", ".").toLowerCase() + "@live.com";

            if (AccountCreator.create(email, name, pass)) {
                AccountManager.setCurrentAccount(new Account(email, pass));
                System.out.println("Set account - " + email + ":" + pass);
            }

        }).start());

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        add(submit, constraints);
    }
}
