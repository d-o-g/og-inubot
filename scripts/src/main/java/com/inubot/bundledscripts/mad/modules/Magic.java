package com.inubot.bundledscripts.mad.modules;

import com.inubot.api.methods.Client;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Mouse;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Spell;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;
import com.inubot.bundledscripts.mad.util.interfaces.Module;

/**
 * Created by mad on 7/25/15.
 */
public class Magic implements Module, Tutorial {

    @Override
    public boolean validate() {
        return setting() < 1000;
    }

    @Override
    public void execute() {

        if(!isChatOpen()) {
            switch (setting()) {

                case 620:
                    interact("Magic Instructor", "Talk-to", new Tile(3141, 3089));
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 630:
                    openTab(Tab.MAGIC);
                    break;
                case 640:
                    interact("Magic Instructor", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 650:
                    final Tile nearChickens = new Tile(3139,3091,0);
                    if(nearChickens.distance() < 2) {
                        Npc npc = Npcs.getNearest("Chicken");
                        if (npc != null) {
                            Client.processAction(1, 14286850, 25, 0, "Cast", "Wind Strike", 50, 50);
                            openTab(Tab.MAGIC);
                            Mouse.setLocation(Interfaces.getWidget(218, 2).getBounds().getLocation().x, Interfaces.getWidget(218, 2).getBounds().getLocation().y);
                            Mouse.click(true);
                            com.inubot.api.methods.Magic.cast(Spell.Modern.WIND_STRIKE, npc);
                            Time.await(() -> setting() != 650, 3200);
                        }
                    } else {
                        Movement.walkTo(nearChickens);
                        Time.await(() -> nearChickens.distance() < 2, 1200);
                    }
                    break;
                case 670:
                    if(!Interfaces.isViewingOptionDialog()) {
                        interact("Magic Instructor", "Talk-to");
                        Time.await(Tutorial::isChatOpen, 1200);
                    } else Interfaces.processDialogOption(0);
                    break;

            }
        } else continueChat();

    }
}
