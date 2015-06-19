package com.inubot.script.bytehound.tks;

import com.inubot.api.oldschool.Tile;
import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;
import com.inubot.script.memes.Action;

/**
 * Created by Cameron on 2015-04-30.
 */
public class SpeakToReldo implements Action {

    @Override
    public boolean validate() {
        return !TheKnightSword.hasDialogue() && TheKnightSword.getQuestStage().equals(TheKnightSword.QuestStage.SPEAK_TO_RELDO);
    }

    @Override
    public void execute() {
        final Npc reldo = Npcs.getNearest("Reldo");
        if (reldo != null) {
            final GameObject door = GameObjects.getNearest("Door");
            if (door != null && door.containsAction("Open")) {
                door.processAction("Open");
                Time.sleep(300, 900);
            }
            reldo.processAction("Talk-to");
            Time.sleep(300, 500);
        } else {
            if (new Tile(2965, 3363).distance() > 100) {
                Movement.walkTo(new Tile(3210, 3489));
            } else {
                Movement.walk(new Tile(2965, 3363), new Tile(2966, 3401), new Tile(3102, 3420), new Tile(3211, 3429), new Tile(3210, 3489));
            }
        }
    }
}
