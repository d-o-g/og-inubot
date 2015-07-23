/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.miner;

import com.inubot.bundledscripts.proframework.ProModel.Event;
import com.inubot.bundledscripts.proframework.ProView;

import javax.swing.*;
import java.awt.*;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public class MinerView extends ProView<MinerController> implements MinerConstants {

    private final JFrame frame;
    private final JList<ProMiner.Location> locationList;
    private final JList<ProMiner.Rock> rockList;
    private final JCheckBox powermining;
    private boolean disposable;

    MinerView() {
        this.locationList = new JList<>(ProMiner.Location.values());
        this.rockList = new JList<>(ProMiner.Rock.values());
        this.powermining = new JCheckBox("Powermining");
        this.frame = new JFrame("ProMiner");
        this.initFrame();
    }

    private void initFrame() {
        frame.setLayout(new BorderLayout());

        JPanel locationContainer = new JPanel();
        locationContainer.setLayout(new BoxLayout(locationContainer, BoxLayout.Y_AXIS));
        locationContainer.add(new JLabel("Choose a location"));
        locationContainer.add(locationList);

        JPanel rockContainer = new JPanel();
        rockContainer.setLayout(new BoxLayout(rockContainer, BoxLayout.Y_AXIS));
        rockContainer.add(new JLabel("Choose a rock (ctrl+click for multiple)"));
        rockContainer.add(rockList);

        JPanel btnContainer = new JPanel();
        btnContainer.setLayout(new BoxLayout(btnContainer, BoxLayout.Y_AXIS));
        btnContainer.add(powermining);
        JButton start = new JButton("Start");
        start.addActionListener(e -> {
            getController().encounter(new Event<>(this, INITIATE_PROP));
            setDisposable(true);
        });
        btnContainer.add(start);

        JScrollPane locationPane = new JScrollPane(locationContainer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane rockPane = new JScrollPane(rockContainer, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        frame.add(locationPane, BorderLayout.WEST);
        frame.add(rockPane, BorderLayout.EAST);
        frame.add(btnContainer, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    @Override
    public void dispose() {
        frame.dispose();
    }

    final boolean isPowermining() {
        return powermining.isSelected();
    }

    final ProMiner.Rock[] getSelectedRocks() {
        return rockList.getSelectedValuesList().toArray(new ProMiner.Rock[rockList.getSelectedValuesList().size()]);
    }

    final ProMiner.Location getSelectedLocation() {
        return locationList.getSelectedValue();
    }

    public boolean isDisposable() {
        return disposable;
    }

    private void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }
}
