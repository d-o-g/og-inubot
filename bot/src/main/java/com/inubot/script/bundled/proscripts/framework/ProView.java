/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.framework;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public abstract class ProView<T extends ProController> {

    private T controller;

    final void setController(T controller) {
        this.controller = controller;
    }

    public final T getController() {
        return controller;
    }

    public abstract void display();
    public abstract void dispose();
}
