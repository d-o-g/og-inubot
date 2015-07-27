package me.mad.modules;

import com.inubot.api.methods.Client;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.util.Time;
import me.mad.util.interfaces.Module;

/**
 * Created by mad on 7/25/15.
 */
public class QuestGuide implements Module {

    @Override
    public boolean validate() {
        return Tutorial.setting() < 260;
    }

    @Override
    public void execute() {

        if(!Tutorial.isChatOpen()) {
            switch (Tutorial.setting()) {
                case 183:
                    Tutorial.openTab(Tab.EMOTES);
                    break;
                case 187:
                    Widget emoteTab = Interfaces.getWidget(216,1);
                    if(emoteTab.isVisible()) {
                        Client.processAction(1, 0, 14155777, 57, "Yes", "", 50, 50);
                    }
                    break;
                case 190:
                    Tutorial.openTab(Tab.OPTIONS);
                    break;
                case 200:
                    Movement.toggleRun(true);
                    break;
                case 210:
                    Tutorial.interactGB("Door", "Open", new Tile(3086, 3126));
                    Time.await(() -> Tutorial.setting() != 210, 1200);
                    break;
                case 220:
                    Tutorial.interact("Quest Guide", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 230:
                    Tutorial.openTab(Tab.QUEST_LIST);
                    break;
                case 240:
                    Tutorial.interact("Quest Guide", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 250:
                    Tutorial.interactGB("Ladder", "Climb-down");
                    Time.await(() -> Tutorial.setting() != 250, 1200);
            }
        } else Tutorial.continueChat();
    }
}
