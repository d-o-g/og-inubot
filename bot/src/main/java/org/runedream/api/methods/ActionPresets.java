/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.methods;

import org.runedream.api.oldschool.actionbar.ActionItem;
import org.runedream.api.oldschool.action.ActionOpcodes;

/**
 * @author unsigned
 * @since 17-05-2015
 */
public interface ActionPresets extends ActionOpcodes {
    ActionItem[] BAR_0 = new ActionItem[]{
            new ActionItem(1, 2558, "Rub", "Ring of dueling(5)")
    };
}
