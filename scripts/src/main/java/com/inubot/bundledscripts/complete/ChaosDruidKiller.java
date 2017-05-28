package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.methods.traversal.Path;
import com.inubot.api.methods.traversal.graph.WebPath;
import com.inubot.api.methods.traversal.graph.data.WebBank;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.private_.BlueDragonKiller;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.util.function.BooleanSupplier;

/**
 * @author Septron
 * @since May 24, 2017
 */
@Manifest(name = "Chaos Druid Killer", developer = "Septron", desc = "")
public class ChaosDruidKiller extends Script {

    private static final String[] LOOT_TABLE = {
            "Grimy harralander", "Grimy avantoe", "Grimy irit leaf", "Grimy kwuarm", "Grimy ranarr weed",
            "Grimy lantadyme", "Grimy dwarf weed", "Grimy candantine", "Law rune", "Nature rune", "Mithril bolts",
            "Runite bar", "Rune battleaxe", "Rune 2h sword", "Dragonstone", "Rune kiteshield", "Dragon med helm",
            "Shield left half", "Tooth half of key", "Dragon spear", "Rune sq shield"
    };

    private static final Tile CHAOS_DRUIDS = new Tile(2933, 9848, 0);
    private static final Tile IN_CAVE = new Tile(2884, 9797);

    private static final String FOOD = "Lobster";

    private enum State {
        BANKING,
        WALKING,
        FIGHTING,
        LOOTING,
        EATING
    }

    private State getState() {
        if (Inventory.isFull() && !Inventory.contains("Lobster")) {
            return State.BANKING;
        } else if (!Inventory.isFull() && Inventory.contains("Lobster") && IN_CAVE.distance() > 100) {
            return State.WALKING;
        } else if (GroundItems.getNearest(new NameFilter<>(LOOT_TABLE)) != null) {
            return State.LOOTING;
        } else if (Skills.getCurrentLevel(Skill.HITPOINTS) < 25) {
            return State.EATING;
        } else if (!Players.getLocal().isMoving() && Players.getLocal().getTarget() == null) {
            return State.FIGHTING;
        }
        return null;
    }

    private void withdrawSetup() {
        Bank.depositInventory();
        WidgetItem food = Bank.getFirst(FOOD);
        WidgetItem tabs = Bank.getFirst("Falador teleport");
        if (food != null && tabs != null) {
            tabs.processAction("Withdraw-1");
            food.processAction("Withdraw-10");
        }
    }

    @Override
    public int loop() {
        State state = getState();
        System.out.println(state);
        switch (state) {
            case WALKING: {
                WebPath path = WebPath.build(IN_CAVE);
                path.step(Path.Option.TOGGLE_RUN);
                return 2500;
            }
            case BANKING: {
                if (CHAOS_DRUIDS.distance() < 30) {
                    WidgetItem item = Inventory.getFirst("Falador teleport");
                    if (item != null) {
                        Tile last = Players.getLocal().getLocation();
                        item.processFirst();
                        Time.await(() -> !Players.getLocal().getLocation().equals(last), 3000);
                    }
                } else if (!Bank.isOpen()) {
                    Bank.open();
                } else {
                    withdrawSetup();
                }
                break;
            }
            case FIGHTING: {
                if (CHAOS_DRUIDS.distance() > 10) {
                    Movement.walkTo(CHAOS_DRUIDS);
                } else {
                    Npc npc = Npcs.getNearest(n ->
                            n.getName().equals("Chaos druid") && n.distance() < 10 && !n.isHealthBarVisible() && !n.isDying()
                    );
                    if (npc != null) {
                        npc.processAction("Attack");
                    }
                }
                break;
            }
            case LOOTING: {
                GroundItem loot = GroundItems.getNearest(LOOT_TABLE);
                if (loot != null) {
                    if (Inventory.isFull()) {
                        WidgetItem lob = Inventory.getFirst(FOOD);
                        if (lob != null) {
                            lob.processAction("Eat");
                        }
                    }
                    loot.processAction("Take");
                    Time.await(() -> !Players.getLocal().isMoving(), 3000);
                }
                break;
            }
            case EATING: {
                WidgetItem food = Inventory.getFirst(FOOD);
                if (food != null) {
                    food.processAction("Eat");
                }
                break;
            }
        }
        return 500;
    }
}
