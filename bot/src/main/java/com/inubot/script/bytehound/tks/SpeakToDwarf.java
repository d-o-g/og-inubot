package com.inubot.script.bytehound.tks;

import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Spell;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.methods.Magic;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.util.Time;
import com.inubot.script.memes.Action;

/**
 * Created by Cameron on 2015-04-30.
 */
public class SpeakToDwarf implements Action {

    @Override
    public boolean validate() {
        return !TheKnightSword.hasDialogue() && (TheKnightSword.getQuestStage().equals(TheKnightSword.QuestStage.SPEAK_TO_DWARF) || TheKnightSword.getQuestStage().equals(TheKnightSword.QuestStage.GIVE_REDBERRY));
    }

    @Override
    public void execute() {
        final Npc thurgo = Npcs.getNearest("Thurgo");
        if (thurgo != null) {
            thurgo.processAction("Talk-to");
        } else {
            final Npc reldo = Npcs.getNearest("Reldo");
            if (reldo != null) {
                Magic.select(Spell.Modern.LUMBRIDGE_HOME_TELEPORT);
                while (Players.getLocal().getAnimation() != -1) {
                    Time.sleep(300, 500);
                }
                if (new Tile(3098, 3230).distance() > 100) {
                    Movement.walkTo(new Tile(2998, 3145));
                } else {
                    Movement.walk(new Tile(3098, 3230), new Tile(3071, 3277), new Tile(3004, 3211), new Tile(2998, 3145));
                }
            } else {
                Movement.walkTo(new Tile(2998, 3145));
            }
        }
    }
}
