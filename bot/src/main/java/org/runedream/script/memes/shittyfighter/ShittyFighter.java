package org.runedream.script.memes.shittyfighter;

import org.runedream.RuneDream;
import org.runedream.api.methods.*;
import org.runedream.api.methods.traversal.Movement;
import org.runedream.api.oldschool.*;
import org.runedream.api.oldschool.action.tree.InputButtonAction;
import org.runedream.api.util.filter.Filter;
import org.runedream.script.Script;

/**
 * Created by luckruns0ut on 01/05/15.
 */
public class ShittyFighter extends Script {

    private static final Filter<Widget> LOBBY_FILTER = w -> w.getText() != null && w.getText().equals("Play RuneScape");
    private static final Filter<Widget> DIALOGUE_FILTER = w -> w.getText() != null && (w.getText().equals("Click here to continue"));
    private String name = "Chicken";

    public boolean setup() {
        if (Skills.getLevel(Skill.ATTACK) >= 10 && Skills.getLevel(Skill.STRENGTH) >= 10 && Skills.getLevel(Skill.DEFENCE) >= 10) {
            name = "Cow";
        }
        return true;
    }

    int loops = 0;

    @Override
    public int loop() {
        if (Interfaces.getWidgets(LOBBY_FILTER).length > 0) {
            for (Widget widget : Interfaces.getWidgets(DIALOGUE_FILTER)) {
                if (widget.getText() == null)
                    continue;
                Client.processAction(new InputButtonAction(widget.getId()), "Play RuneScape", "");
            }
        }

        System.out.println(Combat.getStyle());

        if(Interfaces.canContinue()) {
            Interfaces.clickContinue();
        }

        // writing this while mentally exhausted, pls be nice if its brainfuck
        int att = Skills.getCurrentLevel(Skill.ATTACK);
        int str = Skills.getCurrentLevel(Skill.STRENGTH);
        int def = Skills.getCurrentLevel(Skill.DEFENCE);

        loops++;
        if(loops == 20) {
            if (str < att) {
                RuneDream.getInstance().getClient().processAction(-1, 38862859, 57, 1, "Slash", "", 0, 0);
            } else if (def < str) {
                RuneDream.getInstance().getClient().processAction(-1, 38862863, 57, 1, "Block", "", 0, 0);
            } else {
                RuneDream.getInstance().getClient().processAction(-1, 38862851, 57, 1, "Stab", "", 0, 0);
            }
            loops = 0;
        }

        if(Skills.getCurrentLevel(Skill.HITPOINTS) > 3) {
            Npc npc = Npcs.getNearest(n -> name.equals(n.getName()) && n.getTargetIndex() == -1 && n.getAnimation() == -1 && Players.getLocal().getTargetIndex() == -1);
            if (npc != null && Movement.isReachable(npc)) {
                npc.processAction("Attack");
            }
        }

        return 500;
    }
}
