package com.inubot.bundledscripts.complete.tutisland.tasks;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Tabs;
import com.inubot.api.methods.Varps;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Tab;

/**
 * Created by Cameron on 2015-04-30.
 */
public class QuestGuide extends TutorialIslandTask {

    @Override
    public boolean verify() {
        return Varps.get(281) >= 220 && Varps.get(281) <= 250;
    }

    @Override
    public void run() {
            if (Varps.get(281) == 220 || Varps.get(281) == 240) {
                final Npc questGuide = Npcs.getNearest("Quest Guide");
                if (questGuide != null) {
                    questGuide.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 230) {
                Tabs.open(Tab.QUEST_LIST);
            } else if (Varps.get(281) == 250) {
                final GameObject ladder = GameObjects.getNearest("Ladder");
                if (ladder != null) {
                    ladder.processAction("Climb-down");
                }
            }
    }
}
