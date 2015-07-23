package com.inubot.bundledscripts.complete.tutisland.tasks;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.util.Time;

/**
 * Created by Cameron on 2015-04-30.
 */
public class CombatInstructor extends TutorialIslandTask {

    @Override
    public boolean verify() {
        return Varps.get(281) >= 370 && Varps.get(281) <= 500;
    }

    @Override
    public void run() {
            if (Varps.get(281) == 370 || Varps.get(281) == 410) {
                final Npc combatInstructor = Npcs.getNearest("Combat Instructor");
                if (combatInstructor != null) {
                    combatInstructor.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 390) {
                Tabs.open(Tab.EQUIPMENT);
            } else if (Varps.get(281) == 400) {
                final Widget widget = Interfaces.getWidget(387, 17);
                if (widget != null) {
                    widget.processAction("View equipment stats");
                }
            } else if (Varps.get(281) == 405) {
                final WidgetItem dagger = Inventory.getFirst("Bronze dagger");
                if (dagger != null) {
                    dagger.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
                }
            } else if (Varps.get(281) == 420) {
                System.out.println("Varp 420 activated smoke that marija!");
                final WidgetItem sword = Inventory.getFirst("Bronze sword");
                final WidgetItem shield = Inventory.getFirst("Wooden shield");
                if (sword != null && shield != null) {
                    sword.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
                    shield.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
                }
            } else if (Varps.get(281) == 430) {
                Tabs.open(Tab.COMBAT);
            } else if (Varps.get(281) == 440) {
                Movement.walkTo(new Tile(3111, 9518));
                Time.sleep(400, 600);
                final GameObject gate = GameObjects.getNearest("Gate");
                if (gate != null) {
                    gate.processAction("Open");
                }
            } else if (Varps.get(281) == 450) {
                final Npc rat = Npcs.getNearest(npc -> npc.getName().equals("Giant rat") && npc.getTargetIndex() == -1);
                if (rat != null) {
                    rat.processAction("Attack");
                }
            } else if (Varps.get(281) == 470) {
                final Npc rat = Npcs.getNearest(npc -> npc.getName().equals("Giant rat") && npc.getTargetIndex() == -1);
                if (Movement.isReachable(rat.getLocation())) {
                    final GameObject gate = GameObjects.getNearest("Gate");
                    if (gate != null) {
                        gate.processAction("Open");
                    }
                }
                Time.sleep(1000);
                final Npc combatInstructor = Npcs.getNearest("Combat Instructor");
                if (combatInstructor != null) {
                    combatInstructor.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 480) {
                final WidgetItem shortbow = Inventory.getFirst("Shortbow");
                final WidgetItem arrow = Inventory.getFirst("Bronze arrow");
                if (shortbow != null && arrow != null) {
                    shortbow.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
                    arrow.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
                }
                final Npc rat = Npcs.getNearest(npc -> npc.getName().equals("Giant rat") && npc.getTargetIndex() == -1);
                if (rat != null) {
                    rat.processAction("Attack");
                }
            } else if (Varps.get(281) == 500) {
                final GameObject gate = GameObjects.getNearest("Ladder");
                if (gate != null) {
                    gate.processAction("Climb-up");
                }
            }
    }
}
