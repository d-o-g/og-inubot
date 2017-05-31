/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.InterfaceComponent;

public class Minimap {

    public static int getScale() {
        return Inubot.getInstance().getClient().getMapScale();
    }

    public static int getRotation() {
        return Inubot.getInstance().getClient().getMapRotation();
    }

    public static int getOffset() {
        return Inubot.getInstance().getClient().getMapOffset();
    }

    public static InterfaceComponent getComponent() {
        InterfaceComponent[] children = Interfaces.componentsFor(164);
        for (InterfaceComponent wc : children) {
            if (wc == null || wc.getWidth() != 172)
                continue;
            return wc;
        }
        return null;
    }
}
