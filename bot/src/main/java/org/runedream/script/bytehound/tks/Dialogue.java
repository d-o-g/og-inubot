package org.runedream.script.bytehound.tks;

import org.runedream.api.methods.Interfaces;
import org.runedream.api.oldschool.Widget;
import org.runedream.script.bytehound.tks.TheKnightSword;
import org.runedream.script.memes.Action;

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
