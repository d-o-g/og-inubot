package com.inubot.private_;

import com.inubot.api.methods.*;
import com.inubot.api.methods.Bank;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.methods.traversal.Path.Option;
import com.inubot.api.methods.traversal.graph.WebPath;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.client.natives.oldschool.RSEntityMarker;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.util.ArrayList;
import java.util.List;

/**
 * @author blitz
 * @since 11-07-2015
 */
@Manifest(name = "BlueDragonKiller", developer = "blitz", desc = "Kills blue dragons using safe spots. Keep lobsters and falador teleport tablets in your bank")
public class BlueDragonKiller extends Script {

    //wat else
    private static final String[] LOOT = {"Draconic visage", "Clue scroll (hard)", "Dragon bones",
            "Grimy dwarf weed", "Grimy avantoe", "Grimy ranarr", "Blue dragonhide", "Grimy cadantine",
            "Dragon med helm", "Rune kiteshield", "Dragon spear", "Shield left half", "Rune dagger",
            "Dragonstone", "Rune sq shield", "Rune 2h sword", "Rune battleaxe", "Runite bar",
            "Loop half of key", "Tooth half of key"};
    private static final String[] FOOD = {"Lobster"};
    private static final Tile NORTH_SAFE = new Tile(2900, 9809);
    private static final Tile SOUTH_SAFE = new Tile(-1, -1);
    private static final Tile[] SAFE_SPOTS = {NORTH_SAFE, SOUTH_SAFE};
    private static final Tile IN_CAVE = new Tile(2884, 9797);
    private static final Area CAVE_START = new Area(new Tile(2880, 9793), new Tile(2888, 9810));

    @Override
    public int loop() {
        State state = getState();
        if (state == null)
            return 1000;                     //TODO make it choose a random safe spot every trip?
        switch (getState()) {
            case WALKING: {
                WebPath path = WebPath.build(IN_CAVE);
                path.step(Option.TOGGLE_RUN);
                break;
            }
            case BANKING: {
                if (IN_CAVE.distance() < 30) {
                    Item item = Inventory.getFirst("Falador teleport");
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
                if (CAVE_START.contains(Players.getLocal())) {
                    GameObject obs = GameObjects.getNearest("Obstacle pipe");
                    if (obs != null) {
                        obs.processAction("Squeeze-through");
                    }
                } else if (!Players.getLocal().isMoving() && !Players.getLocal().getLocation().equals(NORTH_SAFE)) {
                    Movement.walkTo(NORTH_SAFE);
                    return 1800;
                } else {
                    Npc npc = Npcs.getNearest(n -> "Blue dragon".equals(n.getName()) && n.distance() < 10
                            && canFire(n.getLocation())
                            && !n.isHealthBarVisible() && !n.isDying());
                    if (npc != null) {
                        npc.processAction("Attack");
                    }
                }
                break;
            }
            case LOOTING: {
                GroundItem loot = GroundItems.getNearest(new NameFilter<>(LOOT));
                if (loot != null) {
                    if (Inventory.isFull()) {
                        Item lob = Inventory.getFirst(new NameFilter<>(FOOD));
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
                Item food = Inventory.getFirst(new NameFilter<>(FOOD));
                if (food != null) {
                    food.processAction("Eat");
                }
                break;
            }
        }
        return 900;
    }

    private boolean canFire(Tile dest) {
        Tile[] bresenham = bresenham(Players.getLocal().getLocation(), dest);
        for (Tile tile : bresenham) {
            if (GameObjects.getNearest(t -> t.getRaw() instanceof RSEntityMarker
                    && t.getLocation().equals(tile)) != null) {
                return false;
            }
        }
        return true;
    }

    private Tile[] bresenham(Tile start, Tile dest) {
        List<Tile> line = new ArrayList<>();
        int dx = Math.abs(dest.getX() - start.getX()), dy = Math.abs(dest.getY() - start.getY());
        int sx = start.getX() < dest.getX() ? 1 : -1, sy = start.getY() < dest.getY() ? 1 : -1;
        int err = dx - dy;
        int e2;
        int currentX = start.getX(), currentY = start.getY();
        while (true) {
            line.add(new Tile(currentX, currentY));
            if (currentX == dest.getX() && currentY == dest.getY()) {
                break;
            }
            e2 = 2 * err;
            if (e2 > -1 * dy) {
                err = err - dy;
                currentX = currentX + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                currentY = currentY + sy;
            }
        }
        return line.toArray(new Tile[line.size()]);
    }

    private void withdrawSetup() {
        Bank.depositInventory();
        Item food = Bank.getFirst(new NameFilter<>(FOOD));
        Item tabs = Bank.getFirst(new NameFilter<>("Falador teleport"));
        Item air = Bank.getFirst(new NameFilter<>("Air rune"));
        Item death = Bank.getFirst(new NameFilter<>("Death rune"));
        if (food != null && tabs != null && air != null && death != null) {
            tabs.processAction("Withdraw-10");
            food.processAction("Withdraw-1");
            air.processAction("Withdraw-All");
            death.processAction("Withdraw-All");
        }
    }

    private State getState() {
        if (Inventory.isFull() && !Inventory.contains("Lobster")) {
            return State.BANKING;
        } else if (!Inventory.isFull() && Inventory.contains("Lobster") && IN_CAVE.distance() > 30) {
            return State.WALKING;
        } else if (GroundItems.getNearest(new NameFilter<>(LOOT)) != null) {
            return State.LOOTING;
        } else if (Skills.getCurrentLevel(Skill.HITPOINTS) < 25) {
            return State.EATING;
        } else if (!Players.getLocal().isMoving() && Players.getLocal().getTarget() == null) {
            return State.FIGHTING;
        }
        return null;
    }

    private enum State {
        BANKING,
        WALKING,
        FIGHTING,
        LOOTING,
        EATING
    }
}
