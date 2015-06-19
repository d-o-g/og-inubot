package org.runedream.bot.ui;

import org.runedream.RuneDream;
import org.runedream.api.methods.Client;
import org.runedream.bot.account.Account;
import org.runedream.bot.account.AccountManager;

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
        run.setEnabled(!RuneDream.getInstance().getScriptFlux().isRunning());
        run.addActionListener(e -> new ScriptSelector().setVisible(true));
        JButton randomAcc = new JButton("Generate Account");
        randomAcc.addActionListener(e -> {
            Account a = AccountManager.generateRandomAccount();
            if (a != null) {
                a.enterCredentials();
            }
        });
        pause.setEnabled(RuneDream.getInstance().getScriptFlux().isRunning());
        pause.addActionListener(e -> {
            RuneDream.getInstance().getScriptFlux().switchState();
            updateButtonStates();
        });
        stop.setEnabled(RuneDream.getInstance().getScriptFlux().isRunning());
        stop.addActionListener(e -> {
            RuneDream.getInstance().getScriptFlux().stop();
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
        add(randomAcc);
    }

    public void updateButtonStates() {
        run.setEnabled(!RuneDream.getInstance().getScriptFlux().isRunning());
        stop.setEnabled(RuneDream.getInstance().getScriptFlux().isRunning());
        pause.setEnabled(RuneDream.getInstance().getScriptFlux().isRunning());
        pause.setText(RuneDream.getInstance().getScriptFlux().isPaused() ? "Resume" : "Pause");
    }
}
