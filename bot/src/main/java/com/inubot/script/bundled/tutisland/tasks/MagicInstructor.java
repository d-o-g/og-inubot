package com.inubot.script.bundled.tutisland.tasks;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Spell;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.util.Time;

/**
 * Created by Cameron on 2015-04-30.
 */
public class MagicInstructor extends TutorialIslandTask {

    @Override
    public boolean verify() {
        return Varps.get(281) >= 620 && Varps.get(281) <= 650;
    }

    @Override
    public void run() {
            if (Varps.get(281) == 620 || Varps.get(281) == 640 || Varps.get(281) == 670) {
                Tile near = new Tile(3133, 3088);
                if (near.distance() > 7)
                    Movement.walkTo(near);
                Time.sleep(500, 800);
                final Npc magicInstructor = Npcs.getNearest("Magic Instructor");
                if (magicInstructor != null) {
                    magicInstructor.processAction("Talk-to");
                }
            } else if (Varps.get(281) == 630) {
                Tabs.open(Tab.MAGIC);
            } else if (Varps.get(281) == 650) {
                Npc npc = Npcs.getNearest(t -> t.getTargetIndex() == -1 && "Chicken".equals(t.getName()));
                Magic.cast(Spell.Modern.WIND_STRIKE, npc);
                //magic.cast(spell, npc);
            }
    }
}