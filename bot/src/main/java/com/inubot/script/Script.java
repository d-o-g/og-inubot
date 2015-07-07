/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script;

import com.inubot.Inubot;
import com.inubot.api.methods.*;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Time;
import com.inubot.bot.account.AccountManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Script extends LoopTask {

    private final List<Task> shutdownTasks = new ArrayList<>();
    private final List<Task> tickTasks = new ArrayList<>();
    private boolean started = false;
    private String username = null, password = null;
    private boolean forceIdleTimeClick = true;

    public boolean setup() {
        return true;
    }

    public void onFinish() {

    }

    public final void run() {
        if (!started) {
            if (started = setup()) {
                if (AccountManager.getCurrentAccount() == null) {
                    username = Inubot.getInstance().getClient().getUsername();
                    password = Inubot.getInstance().getClient().getPassword();
                } else {
                    username = AccountManager.getCurrentAccount().getUsername();
                    password = AccountManager.getCurrentAccount().getPassword();
                }
                if (this instanceof Paintable)
                    Inubot.getInstance().getCanvas().paintables.add((Paintable) this);
                super.run();
            } else {
                Inubot.getInstance().getCanvas().paintables.clear();
                stop();
            }
            throw new RuntimeException("haha i stopped script");
        }
    }

    public final void addShutdownTask(final Task task) {
        this.shutdownTasks.add(task);
    }

    @Override
    public final void onExit() {
        shutdownTasks.forEach(Task::execute);
        if (this instanceof Paintable)
            Inubot.getInstance().getCanvas().paintables.clear();
    }

    @Override
    public final void handleEvents() { //called at the end of every loop
        if (!Game.isLoggedIn()) {
            onLogout();
            if (Login.getState() == Login.STATE_MAIN_MENU) {
                Mouse.move(Login.EXISTING_USER.x, Login.EXISTING_USER.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            } else if (Login.getState() == Login.STATE_CREDENTIALS) {
                Login.setUsername(username);
                Login.setPassword(password);
                Mouse.move(Login.LOGIN.x, Login.LOGIN.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            }
        }
        // anti-logout, clicking stops it from logging you apparently
        if (forceIdleTimeClick) {
            Mouse.move(750, 15);
            Mouse.click(true);
        }
    }

    public void setForceIdleTimeClick(boolean click) {
        this.forceIdleTimeClick = click;
    }

    public void onLogout() {

    }

    public void messageReceived(MessageEvent e) {

    }

    public List<Task> getTickTasks() {
        return tickTasks;
    }
}
