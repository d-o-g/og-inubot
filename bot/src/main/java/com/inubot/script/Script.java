/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script;

import com.inubot.Bot;
import com.inubot.Inubot;
import com.inubot.api.methods.*;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Time;
import com.inubot.bot.account.Account;
import com.inubot.bot.account.AccountManager;
import com.inubot.client.natives.oldschool.RSClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Script extends LoopTask {

    private final List<Task> shutdownTasks = new ArrayList<>();
    private final List<Task> tickTasks = new ArrayList<>();
    private boolean started = false;

    public boolean setup() {
        return true;
    }

    public final void run() {
        if (!started) {
            if (started = setup()) {
                if (AccountManager.getCurrentAccount() == null) {
                    RSClient c = Inubot.getInstance().getClient();
                    AccountManager.setCurrentAccount(new Account(c.getUsername(), c.getPassword()));
                }
                if (this instanceof Paintable) {
                    Bot.getInstance().getCanvas().paintables.add((Paintable) this);
                }
                super.run();
            } else {
                Bot.getInstance().getCanvas().paintables.clear();
                stop();
            }
            System.out.println("Finished script");
        }
    }

    public final void addShutdownTask(final Task task) {
        this.shutdownTasks.add(task);
    }

    @Override
    public final void onExit() {
        shutdownTasks.forEach(Task::execute);
        if (this instanceof Paintable) {
            Bot.getInstance().getCanvas().paintables.clear();
        }
    }

    @Override
    public final void handleEvents() { //called at the end of every loop
        if (!Game.isLoggedIn()) {
            onLogout();
            if (Login.getState() == Login.STATE_MAIN_MENU) {
                Mouse.setLocation(Login.EXISTING_USER.x, Login.EXISTING_USER.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            } else if (Login.getState() == Login.STATE_CREDENTIALS) {
                AccountManager.getCurrentAccount().enterCredentials();
                Mouse.setLocation(Login.LOGIN.x, Login.LOGIN.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            }
        }
        if (!Magic.isSpellSelected()) {
            Inubot.getInstance().getClient().resetMouseIdleTime();
            Mouse.setLocation(750, 15);
            Mouse.click(true);
        }
    }

    public void onLogout() {

    }

    public void messageReceived(MessageEvent e) {

    }

    public List<Task> getTickTasks() {
        return tickTasks;
    }
}
