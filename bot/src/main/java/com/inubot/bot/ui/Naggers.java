package com.inubot.bot.ui;

import com.inubot.Inubot;
import com.inubot.script.Script;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Dank Memes
 * @since June 19, 2015
 */
public class Naggers extends JDialog {

    public static void main(String... args) { new Naggers().setVisible(true);}

    private List<? extends Script> scripts;

    private JComboBox accounts;
    private JButton reload;
    private JButton start;
    private JTable table;

    public Naggers() {
        init();
        load();
    }

    private void init() {
        setLayout(new BorderLayout());


    }

    private void load() {

    }

}
