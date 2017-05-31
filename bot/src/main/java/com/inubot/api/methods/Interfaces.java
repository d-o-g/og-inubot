/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.InterfaceComponent;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.DialogButtonAction;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.client.natives.oldschool.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Interfaces {

    /**
     * Values of the InterfaceComponent#buttonType field (also referred to as actionType) *
     */
    public static final int BUTTON_INPUT = 1;
    public static final int BUTTON_SPELL = 2;
    public static final int BUTTON_CLOSE = 3;
    public static final int BUTTON_VAR_FLIP = 4;
    public static final int BUTTON_VAR_SET = 5;
    public static final int BUTTON_DIALOG = 6;
    private static int continueDialogId = -1;

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

    public static InterfaceComponent get(int parent, int child, int... rest) {
        RSClient client = Game.getClient();
        RSInterfaceComponent[][] all = client.getInterfaceComponents();
        if (all == null || parent >= all.length) return null;
        RSInterfaceComponent[] chi = all[parent];
        if (chi == null) return null;
        RSInterfaceComponent comp = chi[child];
        for (final int child0 : rest) {
            if (comp == null) return null;
            if (comp.getChildren() == null) return null;
            comp = comp.getChildren()[child0];
        }
        return new InterfaceComponent(comp, comp.getIndex());
    }

    public static InterfaceComponent[][] getAll() {
        int l = Inubot.getInstance().getClient().getInterfaceComponents().length;
        InterfaceComponent[][] interfaceComponents = new InterfaceComponent[l][];
        for (int i = 0; i < l; i++) {
            interfaceComponents[i] = componentsFor(i);
        }
        return interfaceComponents;
    }

    public static InterfaceComponent[] componentsFor(int index) {
        RSInterfaceComponent[][] accessor = Inubot.getInstance().getClient().getInterfaceComponents();
        if (accessor == null)
            return new InterfaceComponent[0];
        if (accessor.length < index)
            return new InterfaceComponent[0];
        RSInterfaceComponent[] container = accessor[index];
        if (container == null)
            return new InterfaceComponent[0];
        InterfaceComponent[] children = new InterfaceComponent[container.length];
        for (int child = 0; child < container.length; child++)
            children[child] = container[child] == null ? null : new InterfaceComponent(index, container[child], child);
        return children;
    }

    public static InterfaceComponent getComponent(int parent, int child) {
        InterfaceComponent[] children = componentsFor(parent);
        if (children == null || children.length <= child)
            return null;
        return children[child];
    }

    public static InterfaceComponent getComponent(int parent, Filter<InterfaceComponent> filter) {
        for (InterfaceComponent child : componentsFor(parent)) {
            try {
                if (child != null && filter.accept(child))
                    return child;
                if (child != null) {
                    InterfaceComponent result = child.getComponent(filter);
                    if (result != null)
                        return result;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static InterfaceComponent getComponent(Filter<InterfaceComponent> filter) {
        RSInterface[] raw = raw();
        if (raw == null) return null;
        for (int i = 0; i < raw.length; i++) {
            InterfaceComponent child = getComponent(i, filter);
            if (child != null)
                return child;
        }
        return null;
    }

    public static InterfaceComponent[] getComponents(Filter<InterfaceComponent> filter) {
        List<InterfaceComponent> interfaceComponents = new ArrayList<>();
        RSInterface[] raw = raw();
        if (raw == null) return null;
        for (int i = 0; i < raw.length; i++) {
            InterfaceComponent child = getComponent(i, filter);
            if (child != null)
                interfaceComponents.add(child);
        }
        return interfaceComponents.toArray(new InterfaceComponent[interfaceComponents.size()]);
    }

    public static InterfaceComponent byText(Filter<String> filter) {
        return getComponent(wc -> {
            if (wc != null) {
                String text = wc.getText();
                return text != null && filter.accept(text);
            }
            return false;
        });
    }

    public static InterfaceComponent byText(String text) {
        final Filter<String> filter = s -> s.equals(text);
        return getComponent(wc -> {
            if (wc != null) {
                String text_ = wc.getText();
                return text_ != null && filter.accept(text_);
            }
            return false;
        });
    }

    public static InterfaceComponent byAction(Filter<String> filter) {
        for (InterfaceComponent wc : getComponents(f -> true)) {
            if (wc == null)
                continue;
            String[] actions = wc.getActions();
            if (actions != null && actions.length > 0) {
                for (String action : actions) {
                    if (filter.accept(action))
                        return wc;
                }
            }
            for (InterfaceComponent wcc : wc.getComponents()) {
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
        RSInterface[] raw = raw();
        return raw != null && raw.length >= parent && raw[parent] != null;
    }

    public static InterfaceComponent getContinue() {
        InterfaceComponent w = Interfaces.byText("Click here to continue");
        if (w != null && !w.isHidden() && w.isInteractable()) {
            continueDialogId = w.getId();
            return w;
        }
        return null;
    }

    public static boolean canContinue() {
        InterfaceComponent w = Interfaces.byText("Click here to continue");
        if (w != null && !w.isHidden() && w.isInteractable()) {
            continueDialogId = w.getId();
            return true;
        }
        return false;
    }
    //Action<BUTTON_DIALOG>(id=30,args=[ 0 | -1 | 14221314 ])
    //Action<BUTTON_DIALOG>(id=30,args=[ 0 | -1 | 15007745 ])

    public static boolean processContinue() {
        InterfaceComponent w = getContinue();
        if (w != null) {
            if (Varps.get(281) >= 1000) {
                Game.getCanvas().pressKey(KeyEvent.VK_SPACE, 200);
                Game.getCanvas().releaseKey(KeyEvent.VK_SPACE);
            } else {
                Client.processAction(1, -1, 10485761, ActionOpcodes.COMPONENT_ACTION, "", "", 50, 50);
                Time.sleep(300);
                Client.processAction(new DialogButtonAction(continueDialogId, -1), "Continue", "");
            }
            return true;
        }
        return false;
    }

    public static boolean isViewingOptionDialog() {
        return Interfaces.componentsFor(219).length > 0;
    }

    public static void processDialogOption(int optionIndex) {
        if (isViewingOptionDialog()) {
            Client.processAction(new DialogButtonAction(14352384, optionIndex + 1), "", "");
            Time.sleep(300);
            Client.processAction(1, -1, 10485761, ActionOpcodes.COMPONENT_ACTION, "", "", 50, 50);
        }
    }
}
