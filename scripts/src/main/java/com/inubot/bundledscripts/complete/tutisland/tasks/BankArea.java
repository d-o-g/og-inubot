package com.inubot.bundledscripts.complete.tutisland.tasks;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.tree.DialogButtonAction;
import com.inubot.api.util.Time;

/**
 * Created by Cameron on 2015-04-30.
 */
public class BankArea extends TutorialIslandTask {

    @Override
    public boolean verify() {
        return Varps.get(281) >= 510 && Varps.get(281) <= 610;
    }

    @Override
    public void run() {
            if (Varps.get(281) == 510) {
                Widget w = Interfaces.getWidget(t -> t.getText().equals("Yes") || t.getText().equals("Yes.") && t.isVisible());
                if (w != null) {
                    Client.processAction(new DialogButtonAction(w.getId(), 1), "Continue", "");
                } else {
                    final GameObject bank = GameObjects.getNearest("Bank booth");
                    if (bank != null) {
                        bank.processAction("Use");
                    }
                }
            } else if (Varps.get(281) == 520) {
                final GameObject poll = GameObjects.getNearest("Poll booth");
                if (poll != null) {
                    poll.processAction("Use");
                }
            } else if (Varps.get(281) == 525) {
                Movement.walkTo(new Tile(3124, 3124));
                Time.sleep(300, 600);
                final GameObject door = GameObjects.getNearest(t -> "Door".equals(t.getName()) && t.getRegionX() == 69 && t.getRegionY() == 52);
                if (door != null) {
                    door.processAction("Open");
                }
            } else if (Varps.get(281) == 530) {
                final Npc financialAdviser = Npcs.getNearest("Financial Advisor");
                if (financialAdviser != null) {
                    financialAdviser.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 540) {
                final GameObject door = GameObjects.getNearest(t -> "Door".equals(t.getName()) && t.getRegionX() == 74 && t.getRegionY() == 52);
                if (door != null) {
                    door.processAction("Open");
                }
            } else if (Varps.get(281) == 550) {
                Tile dest = new Tile(3129, 3108);
                if (dest.distance() > 8) {
                    Movement.walkTo(new Tile(3129, 3108));
                }
                Time.sleep(1000, 1600);
                final GameObject door = GameObjects.getNearest("Large door");
                if (door != null && door.containsAction("Open")) {
                    door.processAction("Open");
                }
                final Npc brotherBrace = Npcs.getNearest("Brother Brace");
                if (brotherBrace != null) {
                    brotherBrace.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 560) {
                Tabs.open(Tab.PRAYER);
            } else if (Varps.get(281) == 570 || Varps.get(281) == 600) {
                final Npc brotherBrace = Npcs.getNearest("Brother Brace");
                if (brotherBrace != null) {
                    brotherBrace.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 580) {
                Tabs.open(Tab.FRIENDS_LIST);
            } else if (Varps.get(281) == 590) {
                Tabs.open(Tab.IGNORE_LIST);
            } else if (Varps.get(281) == 610) {
                final GameObject door = GameObjects.getNearest("Door");
                if (door != null) {
                    door.processAction("Open");
                }
            }
    }
}
