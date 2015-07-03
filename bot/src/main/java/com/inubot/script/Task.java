/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script;

public interface Task extends Runnable {

    public abstract void execute();

    default void start() {
        new Thread(this).start();
    }

    default void startLater() {
        final Thread thread;
        (thread = new Thread(this)).start();
        try {
            thread.join();
        } catch (final InterruptedException ignored) {}
    }

    @Override
    default void run() {
        execute();
    }
}

