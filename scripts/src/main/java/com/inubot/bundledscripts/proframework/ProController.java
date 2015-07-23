/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.proframework;

import com.inubot.bundledscripts.proframework.ProModel.Listener;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public abstract class ProController<T extends ProView, K extends ProModel> implements Listener {

    private final T view;
    private final K model;

    public ProController(T view, K model) {
        this.view = view;
        this.view.setController(this);
        this.model = model;
        this.model.addLater(this);
    }

    public final T getView() {
        return view;
    }

    public final K getModel() {
        return model;
    }
}
