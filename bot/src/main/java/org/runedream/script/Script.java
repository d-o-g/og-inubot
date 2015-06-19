/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script;

import org.runedream.RuneDream;
import org.runedream.api.methods.*;
import org.runedream.api.util.*;
import org.runedream.bot.account.AccountManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public abstract class Script extends LoopTask {

    private boolean started = false;
    private final List<Task> shutdownTasks = new ArrayList<>();
    private String username = null, password = null;
    private boolean forceIdleTimeClick = true;
    private final List<Task> tickTasks = new ArrayList<>();

    public boolean setup() {
        return true;
    }

    public void onFinish() {

    }

    public final void run() {
        if (!started) {
            if (started = setup()) {
                if(AccountManager.getCurrentAccount() == null) {
                    username = RuneDream.getInstance().getClient().getUsername();
                    password = RuneDream.getInstance().getClient().getPassword();
                } else {
                    username = AccountManager.getCurrentAccount().getUsername();
                    password = AccountManager.getCurrentAccount().getPassword();
                }
                if (this instanceof Paintable)
                    RuneDream.getInstance().getCanvas().paintables.add((Paintable) this);
                super.run();
            } else {
                RuneDream.getInstance().getCanvas().paintables.clear();
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
            RuneDream.getInstance().getCanvas().paintables.clear();
    }

    @Override
    public final void handleEvents() { //called at the end of every loop
        if (!Game.isLoggedIn()) {
            onLogout();
            if (Login.getState() == Login.STATE_MAIN_MENU) {
                Mouse.hop(Login.EXISTING_USER.x, Login.EXISTING_USER.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            } else if (Login.getState() == Login.STATE_CREDENTIALS) {
                Login.setUsername(username);
                Login.setPassword(password);
                Mouse.hop(Login.LOGIN.x, Login.LOGIN.y);
                Mouse.click(true);
                Time.sleep(600, 700);
            }
        }
        // anti-logout, clicking stops it from logging you apparently
        if (forceIdleTimeClick) {
            Mouse.hop(750, 15);
            Mouse.click(true);
        }
    }

    public void setForceIdleTimeClick(boolean click) {
        this.forceIdleTimeClick = click;
    }

    public void onLogout() {

    }

    public void messageReceived(int type, String sender, String message, String channel) {

    }

    public List<Task> getTickTasks() {
        return tickTasks;
    }
}
