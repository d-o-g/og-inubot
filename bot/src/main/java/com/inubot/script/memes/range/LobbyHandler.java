/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes.range;

import com.inubot.api.oldschool.Widget;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.action.tree.InputButtonAction;
import com.inubot.script.memes.Action;

/**
 * @author unsigned
 * @since 25-04-2015
 */
public class LobbyHandler implements Action {
    @Override
    public boolean validate() {
        return Interfaces.getWidgets(RangeGuild.LOBBY_FILTER).length > 0;
    }

    @Override
    public void execute() {
        for (Widget widget : Interfaces.getWidgets(RangeGuild.DIALOGUE_FILTER)) {
            if (widget.getText() == null)
                continue;
            Client.processAction(new InputButtonAction(widget.getId()), "Play RuneScape", "");
        }
    }
}
