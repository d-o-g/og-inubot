package com.inubot.bot.ui;

import com.inubot.Inubot;
import temp.account.AccountManager;
import com.inubot.api.methods.Client;
import temp.account.Account;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Cameron on 2015-04-26.
 */
public class BotToolBar extends JPanel {

    private JButton run = new JButton("Run"), pause = new JButton("Pause"), stop = new JButton("Stop");
    private NexusTogglableButton painting, farming;

    public BotToolBar() {
        super.setLayout(new FlowLayout());
        run.setEnabled(!Inubot.getInstance().getScriptFlux().isRunning());
        run.addActionListener(e -> new ScriptSelector().setVisible(true));
        pause.setEnabled(Inubot.getInstance().getScriptFlux().isRunning());
        pause.addActionListener(e -> {
            Inubot.getInstance().getScriptFlux().switchState();
            updateButtonStates();
        });
        stop.setEnabled(Inubot.getInstance().getScriptFlux().isRunning());
        stop.addActionListener(e -> {
            Inubot.getInstance().getScriptFlux().stop();
            updateButtonStates();
        });
        painting = new NexusTogglableButton("Painting", () -> Client.PAINTING);
        farming = new NexusTogglableButton("Farming", () -> !Client.LANDSCAPE_RENDERING_ENABLED);
        painting.addActionListener(e -> Client.PAINTING = !Client.PAINTING);
        farming.addActionListener(e -> {
            Client.LANDSCAPE_RENDERING_ENABLED = !Client.LANDSCAPE_RENDERING_ENABLED;
            Client.MODEL_RENDERING_ENABLED = !Client.MODEL_RENDERING_ENABLED;
            Client.GAME_TICK_SLEEP = Client.LANDSCAPE_RENDERING_ENABLED ? -1 : 100;
            Client.setLowMemory(!Client.isLowMemory());
        });
        run.setFocusable(false);
        pause.setFocusable(false);
        stop.setFocusable(false);
        painting.setFocusable(false);
        add(run);
        add(pause);
        add(stop);
        add(painting);
        add(farming);
    }

    public void updateButtonStates() {
        run.setEnabled(!Inubot.getInstance().getScriptFlux().isRunning());
        stop.setEnabled(Inubot.getInstance().getScriptFlux().isRunning());
        pause.setEnabled(Inubot.getInstance().getScriptFlux().isRunning());
        pause.setText(Inubot.getInstance().getScriptFlux().isPaused() ? "Resume" : "Pause");
    }
}
