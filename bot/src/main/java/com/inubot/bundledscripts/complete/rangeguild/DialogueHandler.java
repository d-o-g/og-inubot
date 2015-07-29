package com.inubot.bundledscripts.complete.rangeguild;

import com.inubot.Bot;
import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.action.tree.DialogButtonAction;

import java.awt.event.KeyEvent;

/**
 * Created by Cameron on 2015-04-24.
 */
public class DialogueHandler implements Action {

    @Override
    public boolean validate() {
        return Interfaces.getWidgets(RangeGuild.DIALOGUE_FILTER).length > 0;
    }

    @Override
    public void execute() {
        for (Widget widget : Interfaces.getWidgets(RangeGuild.DIALOGUE_FILTER)) {
            if (widget.getText() != null && widget.isVisible()) {
                if (widget.getText().contains("Click here to continue")) {
                    Game.getCanvas().pressKey(KeyEvent.VK_SPACE, 200);
                    Game.getCanvas().releaseKey(KeyEvent.VK_SPACE);
                } else {
                    Client.processAction(new DialogButtonAction(widget.getId(), 1), "Continue", "");
                    break;
                }
            }
        }
    }
}
