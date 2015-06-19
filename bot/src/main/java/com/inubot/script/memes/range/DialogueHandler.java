package com.inubot.script.memes.range;

import com.inubot.api.methods.Client;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.script.memes.Action;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.action.tree.DialogButtonAction;
import com.inubot.script.memes.Action;

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
            if (widget.getText() == null)
                continue;
            if (widget.getText().contains("Click here to continue")) {
                Client.processAction(DialogButtonAction.clickHereToContinue(widget.getId()), "Continue", "");
            } else {
                Client.processAction(new DialogButtonAction(widget.getId(), 1), "Continue", "");
            }
        }
    }
}
