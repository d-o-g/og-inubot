package org.runedream.script.bytehound;

import org.runedream.api.methods.*;
import org.runedream.api.methods.traversal.Movement;
import org.runedream.api.oldschool.Npc;
import org.runedream.api.oldschool.Skill;
import org.runedream.api.oldschool.Widget;
import org.runedream.api.oldschool.action.tree.InputButtonAction;
import org.runedream.api.util.AWTUtil;
import org.runedream.api.util.Paintable;
import org.runedream.api.util.Time;
import org.runedream.api.util.filter.Filter;
import org.runedream.script.Script;

import java.awt.*;

/**
 * Created by Cameron on 2015-05-01.
 */
public class Seagull extends Script implements Paintable {

    private final int startHealthExperience = Skills.getExperience(Skill.HITPOINTS);
    private final int startAttackExperience = Skills.getExperience(Skill.ATTACK);
    private final int startStrengthExperience = Skills.getExperience(Skill.STRENGTH);
    private final int startDefenceExperience = Skills.getExperience(Skill.DEFENCE);

    public static final Filter<Widget> DIALOGUE_FILTER = w -> w.getText() != null && (w.getText().equals("Click here to continue") || w.getText().equals("Sure, I'll give it a go."));
    public static final Filter<Widget> LOBBY_FILTER = w -> w.getText() != null && w.getText().equals("Play RuneScape");

    @Override
    public int loop() {
        if (Game.isLoggedIn()) {
            if (Skills.getLevel(Skill.ATTACK) >= 5 && Inventory.contains("Steel scimitar")) {
                Inventory.getFirst("Steel scimitar").processAction("Equip");
            }

            if (Interfaces.canContinue()) {
                Interfaces.clickContinue();
            }
            if (Interfaces.getWidgets(LOBBY_FILTER).length > 0) {
                for (Widget widget : Interfaces.getWidgets(DIALOGUE_FILTER)) {
                    if (widget.getText() == null)
                        continue;
                    Client.processAction(new InputButtonAction(widget.getId()), "Play RuneScape", "");
                }
            }
            if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 10) {
                Mouse.hop(578, 138);
                Mouse.click(true);
            }
            if (Players.getLocal() != null && Players.getLocal().getTargetIndex() == -1) {
                Npc npc = Npcs.getNearest(n -> {
                    if (n.getMaxHealth() > 0 && n.getHealth() <= 0)
                        return false;
                    String name = n.getName();
                    return name != null && n.getTargetIndex() == -1 && (name.equals("Seagull"));
                });
                if (npc != null) {
                    npc.processAction("Attack");
                }
            }
        }
        return 400;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "AutoSeagulls FREE", 20, 40, Color.MAGENTA);
        AWTUtil.drawBoldedString(g, "Attack: " + (Skills.getExperience(Skill.ATTACK) - startAttackExperience), 20, 60, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Strength: " + (Skills.getExperience(Skill.STRENGTH) - startStrengthExperience), 20, 80, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Defence: " + (Skills.getExperience(Skill.DEFENCE) - startDefenceExperience), 20, 100, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Health: " + (Skills.getExperience(Skill.HITPOINTS) - startHealthExperience), 20, 120, Color.YELLOW);
    }
}
