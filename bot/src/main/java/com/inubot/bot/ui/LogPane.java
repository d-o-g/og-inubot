/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogPane extends JTextPane {

    private static final DateFormat FORMAT = new SimpleDateFormat("HH:mm");
    private final JScrollPane scrollPane;
    private boolean toggled = true;

    public LogPane() {
        System.setOut(new PrintStream(new PipedOutputStream()) {
            @Override
            public void println(String msg) {
                LogPane.this.write(msg);
            }
        });
        StyledDocument document = new DefaultStyledDocument();
        Style style = document.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        super.setDocument(document);
        super.setEditable(false);
        super.setFont(new Font("Dialog", Font.PLAIN, 12));
        super.setPreferredSize(new Dimension(745, 80));
        super.setVisible(true);
        this.scrollPane = new JScrollPane(this);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setValue(100);
        scrollPane.setVisible(true);
    }

    public void write(String msg) {
        try {
            Document doc = super.getDocument();
            Date date = new Date();
            String message = "[" + FORMAT.format(date) + "] - " + msg;
            String insertion = doc.getLength() > 0 ? "\n" + message : message;
            doc.insertString(doc.getLength(), insertion, null);
            if (doc.getLength() > 0)
                super.setCaretPosition(doc.getLength() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}