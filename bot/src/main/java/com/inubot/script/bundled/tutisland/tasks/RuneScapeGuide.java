package com.inubot.script.bundled.tutisland.tasks;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Tab;

/**
 * Created by Cameron on 2015-04-30.
 */
public class RuneScapeGuide extends TutorialIslandTask {

    @Override
    public boolean verify() {
        Widget w = Interfaces.getWidgetByText(s -> s.equals("Use the buttons below to design your player"));
        return Varps.get(281) <= 10 && (w == null || !w.validate() || !w.isVisible());
    }

    @Override
    public void run() {
        if (Varps.get(281) == 0 || Varps.get(281) == 7) {
            final Npc runescapeGuide = Npcs.getNearest("RuneScape Guide");
            if (runescapeGuide != null) {
                runescapeGuide.processAction("Talk-to");
            }
        } else if (Varps.get(281) == 3) {
            Tabs.open(Tab.OPTIONS);
        } else if (Varps.get(281) == 10) {
            final GameObject exitDoor = GameObjects.getNearest("Door");
            if (exitDoor != null) {
                exitDoor.processAction("Open");
            }
        }
    }
}
