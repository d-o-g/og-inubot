/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.Widget;
import com.inubot.client.natives.RSWidget;
import com.inubot.api.oldschool.action.tree.DialogButtonAction;
import com.inubot.api.util.filter.Filter;
import com.inubot.client.natives.RSInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Interfaces {

    /**
     * Values of the Widget#buttonType field (also referred to as actionType) *
     */
    public static final int BUTTON_INPUT = 1;
    public static final int BUTTON_SPELL = 2;
    public static final int BUTTON_CLOSE = 3;
    public static final int BUTTON_VAR_FLIP = 4;
    public static final int BUTTON_VAR_SET = 5;
    public static final int BUTTON_DIALOG = 6;

    public static RSInterface[] raw() {
        return Inubot.getInstance().getClient().getInterfaces();
    }

    public static RSInterface getContainer(int index) {
        RSInterface[] raw = raw();
        if (raw == null)
            return null;
        for (RSInterface face : raw) {
            if (face.getIndex() == index)
                return face;
        }
        return null;
    }

    public static Widget[][] getAll() {
        int l = Inubot.getInstance().getClient().getWidgets().length;
        Widget[][] widgets = new Widget[l][];
        for (int i = 0; i < l; i++) {
            widgets[i] = widgetsFor(i);
        }
        return widgets;
    }

    public static Widget[] widgetsFor(int index) {
        RSWidget[][] accessor = Inubot.getInstance().getClient().getWidgets();
        if (accessor == null)
            return null;
        if (accessor.length < index)
            return new Widget[0];
        RSWidget[] container = accessor[index];
        if (container == null)
            return new Widget[0];
        Widget[] children = new Widget[container.length];
        for (int child = 0; child < container.length; child++)
            children[child] = container[child] == null ? null : new Widget(index, container[child], child);
        return children;
    }

    public static Widget getWidget(int parent, int child) {
        Widget[] children = widgetsFor(parent);
        if (children == null || children.length <= child)
            return null;
        return children[child];
    }

    public static Widget getWidget(int parent, Filter<Widget> filter) {
        for (Widget child : widgetsFor(parent)) {
            try {
                if (child != null && filter.accept(child))
                    return child;
                if (child != null) {
                    Widget result = child.getChild(filter);
                    if (result != null)
                        return result;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static Widget getWidget(Filter<Widget> filter) {
        RSInterface[] raw = raw();
        if (raw == null) return null;
        for (int i = 0; i < raw.length; i++) {
            Widget child = getWidget(i, filter);
            if (child != null)
                return child;
        }
        return null;
    }

    public static Widget[] getWidgets(Filter<Widget> filter) {
        List<Widget> widgets = new ArrayList<>();
        RSInterface[] raw = raw();
        if (raw == null) return null;
        for (int i = 0; i < raw.length; i++) {
            Widget child = getWidget(i, filter);
            if (child != null)
                widgets.add(child);
        }
        return widgets.toArray(new Widget[widgets.size()]);
    }

    public static Widget getWidgetByText(Filter<String> filter) {
        return getWidget(wc -> {
            if (wc != null) {
                String text = wc.getText();
                return text != null && filter.accept(text);
            }
            return false;
        });
    }

    public static Widget getWidgetByText(String widgetTtext) {
        final Filter<String> filter = s -> s.equals(widgetTtext);
        return getWidget(wc -> {
            if (wc != null) {
                String text = wc.getText();
                return text != null && filter.accept(text);
            }
            return false;
        });
    }

    public static Widget getWidgetByAction(Filter<String> filter) {
        for (Widget wc : getWidgets(f -> true)) {
            if (wc == null)
                continue;
            String[] actions = wc.getActions();
            if (actions != null && actions.length > 0) {
                for (String action : actions) {
                    if (filter.accept(action))
                        return wc;
                }
            }
            for (Widget wcc : wc.getChildren()) {
                String[] actions0 = wcc.getActions();
                if (actions0 != null && actions0.length > 0) {
                    for (String action : actions0) {
                        if (filter.accept(action))
                            return wc;
                    }
                }
            }
        }
        return null;
    }

    public static boolean validate(int parent) {
        RSInterface[] widgets = raw();
        return widgets != null && widgets.length >= parent && widgets[parent] != null;
    }

    public static boolean canContinue() {
        return getWidgets(w -> w.getY() > 330 && w.getText().contains("Click here to continue")).length > 0;
    }

    public static boolean clickContinue() {
        boolean ret = false;
        for (Widget widget : getWidgets(t -> t.getText().contains("continue") && t.getY() > 330 && t.isVisible())) {
            Client.processAction(DialogButtonAction.clickHereToContinue(widget.getId()), "Continue", "");
            ret = true;
        }
        return ret;
    }

}
