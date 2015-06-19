package org.runedream.script.bytehound.tks;

import org.runedream.api.methods.Npcs;
import org.runedream.api.oldschool.Npc;
import org.runedream.api.util.Time;
import org.runedream.script.memes.Action;

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
