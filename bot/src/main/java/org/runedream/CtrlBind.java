/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream;

import builder.map.RSMap;
import org.runedream.api.methods.*;
import org.runedream.api.methods.traversal.Movement;
import org.runedream.api.oldschool.GameObject.Landmark;
import org.runedream.api.oldschool.Tile;
import org.runedream.api.oldschool.VarpBit;
import org.runedream.api.oldschool.action.Processable;
import org.runedream.api.util.Time;
import org.runedream.api.util.filter.NameFilter;
import org.runedream.client.natives.RSVarpBit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * @author unsigned
 * @since 16-05-2015
 */
public enum CtrlBind {

    VAR_LISTENER(KeyEvent.VK_Q) {
        @Override
        public void onActivation() {
            List<VarpBit>[] cache = new List[2000];
            for (int i = 0; i < 10000; i++) {
                VarpBit bit = VarpBit.get(i);
                if (bit == null) continue;
                if (cache[bit.getVarpIndex()] == null) {
                    cache[bit.getVarpIndex()] = new ArrayList<>();
                }
                cache[bit.getVarpIndex()].add(bit);
            }
            System.out.println("Cache Built.... Listening");
            int[] vars = RuneDream.getInstance().getClient().getVarps();
            int[] copy = Arrays.copyOf(vars, vars.length);
            new Thread(() -> {
                while (true) {
                    for (int i = 0; i < 2000; i++) {
                        if (copy[i] != vars[i]) {
                            System.out.println("Varp Changed(index=" + i + ")[" + copy[i] + " -> " + vars[i] + "]");
                            List<VarpBit> varps = cache[i];
                            if (varps == null) {
                                copy[i] = vars[i];
                                continue;
                            }
                            for (VarpBit v : varps) {
                                int old = v.getValue(copy[i]);
                                int now = v.getValue(vars[i]);
                                if (old != now)
                                    System.out.println(v.toString());
                            }
                            System.out.println("--------------------------------------------------------------");
                            copy[i] = vars[i];
                        }
                    }
                    Time.sleep(50);
                }
            }).start();
        }
    }, DROP_INV(KeyEvent.VK_X) {
        @Override
        public void onActivation() {
            Inventory.dropAllExcept(new NameFilter<>("Coins"));
        }
    }, MAP_VIEW(KeyEvent.VK_Z) {
        @Override
        public void onActivation() {
            new RSMap();
        }
    }, POSITION(KeyEvent.VK_E) {
        @Override
        public void onActivation() {
            System.out.println(Players.getLocal().getLocation());
        }
    }, WALK_NORTH(KeyEvent.VK_W) {
        @Override
        public void onActivation() {
            Movement.walkTo(new Tile(Players.getLocal().getX(), Players.getLocal().getY() + 5));
        }
    }, WALK_WEST(KeyEvent.VK_A) {
        @Override
        public void onActivation() {
            Movement.walkTo(new Tile(Players.getLocal().getX() - 5, Players.getLocal().getY()));
        }
    }, WALK_SOUTH(KeyEvent.VK_S) {
        @Override
        public void onActivation() {
            Movement.walkTo(new Tile(Players.getLocal().getX(), Players.getLocal().getY() - 5));
        }
    }, WALK_EAST(KeyEvent.VK_D) {
        @Override
        public void onActivation() {
            Movement.walkTo(new Tile(Players.getLocal().getX() + 5, Players.getLocal().getY()));
        }
    }, TOGGLE_ACTION_BAR(KeyEvent.VK_0) {
        @Override
        public void onActivation() {
            ActionBar.setVisible(!ActionBar.isVisible());
        }
    }, ACTION_1(KeyEvent.VK_1) {
        @Override
        public void onActivation() {
            ActionBar.select(1);
        }
    }, ACTION_2(KeyEvent.VK_2) {
        @Override
        public void onActivation() {
            ActionBar.select(2);
        }
    }, ACTION_3(KeyEvent.VK_3) {
        @Override
        public void onActivation() {
            ActionBar.select(3);
        }
    }, ACTION_4(KeyEvent.VK_4) {
        @Override
        public void onActivation() {
            ActionBar.select(4);
        }
    }, ACTION_5(KeyEvent.VK_5) {
        @Override
        public void onActivation() {
            ActionBar.select(5);
        }
    }, ACTION_6(KeyEvent.VK_6) {
        @Override
        public void onActivation() {
            ActionBar.select(6);
        }
    }, ACTION_7(KeyEvent.VK_7) {
        @Override
        public void onActivation() {
            ActionBar.select(7);
        }
    }, ACTION_8(KeyEvent.VK_8) {
        @Override
        public void onActivation() {
            ActionBar.select(8);
        }
    }, ACTION_9(KeyEvent.VK_9) {
        @Override
        public void onActivation() {
            ActionBar.select(9);
        }
    }, TEST_INPUT(KeyEvent.VK_K) {
        @Override
        public void onActivation() {
            for (char c : "13".toCharArray())
                RuneDream.getInstance().getCanvas().sendKey(c, 20);
            RuneDream.getInstance().getCanvas().pressEnter();
        }
    }, OPEN_AMYLASE_PACKS(KeyEvent.VK_O) {
        @Override
        public void onActivation() {
            Inventory.apply(new NameFilter<>("Amylase pack"), item -> item.processAction("Open"));
        }
    }, OPEN_NEAREST_BANK_OBJECT(KeyEvent.VK_B) {
        @Override
        public void onActivation() {
            Processable p = GameObjects.getNearest(Landmark.BANK);
            if (p == null)
                p = Npcs.getNearest(new NameFilter<>("Banker", "Emerald Benedict"));
            if (p == null)
                return;
            p.processAction("Bank");
        }
    }, DRAW(KeyEvent.VK_J) {
        @Override
        public void onActivation() {
            new Thread(() -> {
                while (true) {
                    RuneDream.getInstance().getClient().getWidgets()[149][0].getStackSizes()[0] = 1284765393;
                    RuneDream.getInstance().getClient().getWidgets()[149][0].getItemIds()[1] = 11804;
                    RuneDream.getInstance().getClient().getWidgets()[149][0].getStackSizes()[1] = 128;
                    RuneDream.getInstance().getClient().getWidgets()[149][0].getItemIds()[2] = 12819;
                    RuneDream.getInstance().getClient().getWidgets()[149][0].getStackSizes()[2] = 12;
                    //RuneDream.getInstance().getClient().getFont_p12full().drawString("Penis", 50, 50);
                    //RuneDream.getInstance().getClient().drawRectangle(400, 300, 200, 200, Color.RED.getRGB());
                }
            }).start();
        }
    };

    private final int key;

    private CtrlBind(int key) {
        this.key = key;
    }

    public abstract void onActivation();

    public int getKey() {
        return key;
    }
}
