package com.inubot.script.bundled.tutisland.tasks;

import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;

/**
 * Created by luckruns0ut on 29/04/15.
 */
public class CharacterCustomizer extends TutorialIslandTask {
    @Override
    public boolean verify() {
        Widget w = Interfaces.getWidgetByText(s -> s.equals("Use the buttons below to design your player"));
        return w != null && w.validate() && w.isVisible();
    }

    @Override
    public void run() {
        Widget w = Interfaces.getWidgetByText(s -> s.equals("Accept"));
        if(w != null) {
            w.processAction("Accept");
            return;
        }
    }
}
