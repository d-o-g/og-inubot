package org.runedream.script.bytehound.tks;

import org.runedream.api.methods.Interfaces;
import org.runedream.api.methods.Varps;
import org.runedream.api.oldschool.Widget;
import org.runedream.api.util.AWTUtil;
import org.runedream.api.util.Paintable;
import org.runedream.api.util.filter.Filter;
import org.runedream.script.Script;
import org.runedream.script.memes.Action;

import java.awt.*;
import java.util.*;

/**
 * Created by Cameron on 2015-04-30.
 */
public class TheKnightSword extends Script implements Paintable {

    private final java.util.List<Action> actionList = new ArrayList<>();

    @Override
    public int loop() {
        while (Interfaces.canContinue()) {
            Interfaces.clickContinue();
        }
        return 5000;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "Varp #7: " + Varps.get(7), 20, 20, Color.CYAN);
        AWTUtil.drawBoldedString(g, "Varp #122: " + Varps.get(122), 20, 40, Color.CYAN);
    }

    public static Filter<Widget> DIALOGUE_FILTER = new Filter<Widget>() {
        @Override
        public boolean accept(Widget w) {
            return (w.getText().equals("And how is life as a squire?") ||
                    w.getText().equals("I can make a new sword if you like...")) ||
                    w.getText().equals("So would these dwarves make another one?") ||
                    w.getText().equals("Ok, I'll give it a go.") ||
                    w.getText().equals("What do you know about the Imcando dwarves?") ||
                    w.getText().equals("Something else.") ||
                    w.getText().equals("Would you like some redberry pie?")
                    && w.validate() && w.isVisible() && w != null;
        }
    };

    public static QuestStage getQuestStage() {
        for (QuestStage stage : QuestStage.values()) {
            if (stage.getVarpValue() == Varps.get(122)) {
                return stage;
            }
        }
        return null;
    }

    public static boolean hasDialogue() {
        return Interfaces.canContinue() || Interfaces.getWidgets(TheKnightSword.DIALOGUE_FILTER).length > 0;
    }

    public enum QuestStage {

        START(0),
        SPEAK_TO_RELDO(1),
        GIVE_REDBERRY(2),
        SPEAK_TO_DWARF(3),
        GET_PICTURE(4);

        private int varpValue;

        QuestStage(int varpValue) {
            this.varpValue = varpValue;
        }

        public int getVarpValue() {
            return varpValue;
        }
    }
}
