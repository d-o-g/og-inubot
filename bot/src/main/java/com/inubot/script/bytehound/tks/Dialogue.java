package com.inubot.script.bytehound.tks;

import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.script.memes.Action;

/**
 * Created by Cameron on 2015-04-30.
 */
public class Dialogue implements Action {

    @Override
    public boolean validate() {
        return TheKnightSword.hasDialogue();
    }

    @Override
    public void execute() {
        while (Interfaces.canContinue()) {
            Interfaces.clickContinue();
        }
        final Widget widget = Interfaces.getWidget(TheKnightSword.DIALOGUE_FILTER);
        if (widget != null) {
            widget.processAction("Continue");
        }
    }

}
