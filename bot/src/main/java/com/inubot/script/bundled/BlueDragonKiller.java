/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled;

import com.inubot.Inubot;
import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.methods.traversal.Path.Option;
import com.inubot.api.methods.traversal.graph.WebPath;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.client.natives.RSCollisionMap;
import com.inubot.script.Script;

/**
 * @author Dogerina
 * @since 11-07-2015
 */
public class BlueDragonKiller extends Script {

    //wat else
    private static final String[] LOOT = {"Blue dragonhide", "Dragon bones", "Grimy avantoe", "Grimy ranarr",
            "Draconic visage"};
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
            return 2000;
        switch (getState()) {
            case WALKING: {
                WebPath path = WebPath.build(IN_CAVE);
                path.step(Option.TOGGLE_RUN);
                break;
            }
            case BANKING: {
                if (IN_CAVE.distance() < 30) {
                    WidgetItem item = Inventory.getFirst("Falador teleport");
                    if (item != null) {
                        item.processAction("Break");
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
                } else {
                    if (!Players.getLocal().getLocation().equals(NORTH_SAFE)) { //TODO make it choose a random safe spot?
                        Movement.walkTo(NORTH_SAFE);
                        return 1800;
                    }                           //TODO
                    Npc npc = Npcs.getNearest(n -> Movement.isEntityReachable(n)
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
                        WidgetItem lob = Inventory.getFirst(new NameFilter<>(FOOD));
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
                WidgetItem food = Inventory.getFirst(new NameFilter<>(FOOD));
                if (food != null) {
                    food.processAction("Eat");
                }
                break;
            }
        }
        return 2000;
    }

    private void withdrawSetup() {
        Bank.depositInventory();
        WidgetItem food = Bank.getFirst(new NameFilter<>(FOOD));
        WidgetItem tabs = Bank.getFirst(new NameFilter<>("Falador teleport"));
        if (food != null && tabs != null) {
            tabs.processAction("Withdraw-10");
            food.processAction("Withdraw-5");
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
