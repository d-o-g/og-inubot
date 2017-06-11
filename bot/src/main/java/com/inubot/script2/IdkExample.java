package com.inubot.script2;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Item;
import com.inubot.api.oldschool.Npc;

import java.util.function.Supplier;

public class IdkExample extends Script {

    private static final Supplier<Npc> FISHING_SPOT = () -> Npcs.getNearest("Fishing spot");
    private static final Supplier<Item> FISH = () -> Inventory.getFirst("Shrimp");

    private static final ScriptEvent<Item> DROP_ORE
            = new ScriptEvent<>(FISH, ore -> ore.processAction("Drop"));

    {
        ScriptEvent<Npc> fishShrimps = new ScriptEvent<>(FISHING_SPOT, spot -> spot.processAction("Net"));
        fishShrimps.setCondition(() -> !Inventory.isFull() && Players.getLocal().getAnimation() == -1);
        immediatelyQueue(fishShrimps);

        DROP_ORE.setCondition(Inventory::isFull);
        DROP_ORE.delayCycle(1);
        delayAndQueue(DROP_ORE);
    }
}
