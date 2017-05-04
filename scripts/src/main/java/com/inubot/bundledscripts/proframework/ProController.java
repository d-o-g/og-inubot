package com.inubot.bundledscripts.proframework;

import com.inubot.bundledscripts.proframework.ProModel.Listener;

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
