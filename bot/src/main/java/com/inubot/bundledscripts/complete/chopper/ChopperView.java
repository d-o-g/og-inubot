/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.bundledscripts.proframework.ProModel;
import com.inubot.bundledscripts.proframework.ProModel.*;
import com.inubot.bundledscripts.proframework.ProView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public class ChopperView extends ProView<ChopperController> implements ChopperConstants {

    private final JFrame frame;
    private boolean banking;
    private boolean progressive;
    private boolean disposable;

    ChopperView() {
        this.frame = new JFrame("ProChopper");
        frame.getContentPane().setLayout(new FlowLayout());
        JCheckBox powerchopping = new JCheckBox("Powerchopping");
        JCheckBox progressive = new JCheckBox("Progressive");
        JButton start = new JButton("Start");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDisposable(true);
                setBanking(!powerchopping.isSelected());
                setProgressive(progressive.isSelected());
                getController().encounter(new ProModel.Event<>(this, START_PROP));
            }
        });
        frame.getContentPane().add(powerchopping);
        frame.getContentPane().add(progressive);
        frame.getContentPane().add(start);
    }

    @Override
    public void display() {

    }

    @Override
    public void dispose() {

    }

    public boolean isDisposable() {
        return disposable;
    }

    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }

    public boolean isProgressive() {
        return progressive;
    }

    public void setProgressive(boolean progressive) {
        this.progressive = progressive;
    }

    public boolean isBanking() {
        return banking;
    }

    public void setBanking(boolean banking) {
        this.banking = banking;
    }
}
