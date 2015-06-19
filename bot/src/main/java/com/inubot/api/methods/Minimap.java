/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.RuneDream;
import com.inubot.api.oldschool.Widget;
import com.inubot.RuneDream;
import com.inubot.api.oldschool.Widget;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Minimap {

    public static int getScale() {
        return RuneDream.getInstance().getClient().getMapScale();
    }

    public static int getRotation() {
        return RuneDream.getInstance().getClient().getMapAngle();
    }

    public static int getOffset() {
        return RuneDream.getInstance().getClient().getMapOffset();
    }

    public static Widget getWidget() {
        Widget[] children = Interfaces.widgetsFor(164);
        for (Widget wc : children) {
            if (wc == null || wc.getWidth() != 172)
                continue;
            return wc;
        }
        return null;
    }
}
