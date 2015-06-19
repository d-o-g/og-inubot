package org.runedream.script.memes.tutisland.tasks;

import org.runedream.api.methods.*;
import org.runedream.api.oldschool.GameObject;
import org.runedream.api.oldschool.Npc;
import org.runedream.api.oldschool.Tab;
import org.runedream.api.oldschool.Widget;

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
        if (Interfaces.canContinue()) {
            Interfaces.clickContinue();
            return;
        }
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
