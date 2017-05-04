package com.inubot.bundledscripts.proframework;

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
