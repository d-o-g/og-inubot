/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.range;

import org.runedream.api.methods.Client;
import org.runedream.api.methods.Interfaces;
import org.runedream.api.oldschool.Widget;
import org.runedream.api.oldschool.action.tree.InputButtonAction;
import org.runedream.script.memes.Action;

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
