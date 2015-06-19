package com.inubot.script.bytehound.tks;

import com.inubot.api.methods.Npcs;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.util.Time;
import com.inubot.script.memes.Action;

/**
 * Created by Cameron on 2015-04-30.
 */
public class StartQuest implements Action {

    @Override
    public boolean validate() {
        return !TheKnightSword.hasDialogue() && TheKnightSword.getQuestStage().equals(TheKnightSword.QuestStage.START);
    }

    @Override
    public void execute() {
        final Npc squire = Npcs.getNearest("Squire");
        if (squire != null) {
            squire.processAction("Talk-to");
            Time.sleep(300, 500);
        }
    }
}
