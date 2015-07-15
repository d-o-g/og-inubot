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
public abstract class ProView<T extends ProPresenter> {

    private T presenter;

    final void setPresenter(T presenter) {
        this.presenter = presenter;
    }

    public final T getPresenter() {
        return presenter;
    }

    public abstract void display();
    public abstract void dispose();
}
