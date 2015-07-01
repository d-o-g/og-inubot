/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.VarpBit;
import com.inubot.api.util.Time;
import com.inubot.api.oldschool.GameObject;
import com.inubot.bot.net.acc.CreaterGUI;
import com.inubot.bot.ui.WidgetExplorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * @author unsigned
 * @since 16-05-2015
 */
public enum Hotkey {

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
            Inubot.LOGGER.debug("Cache Built.... Listening");
            int[] vars = Inubot.getInstance().getClient().getVarps();
            int[] copy = Arrays.copyOf(vars, vars.length);
            new Thread(() -> {
                while (true) {
                    for (int i = 0; i < 2000; i++) {
                        if (copy[i] != vars[i]) {
                            Inubot.LOGGER.debug("Varp Changed(index=" + i + ")[" + copy[i] + " -> " + vars[i] + "]");
                            List<VarpBit> varps = cache[i];
                            if (varps == null) {
                                copy[i] = vars[i];
                                continue;
                            }
                            for (VarpBit v : varps) {
                                int old = v.getValue(copy[i]);
                                int now = v.getValue(vars[i]);
                                if (old != now)
                                    Inubot.LOGGER.debug(v.toString());
                            }
                            Inubot.LOGGER.debug("--------------------------------------------------------------");
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
    }, POSITION(KeyEvent.VK_E) {
        @Override
        public void onActivation() {
            Inubot.LOGGER.debug("Current location: " + Players.getLocal().getLocation());
            System.out.println("Current location: " + Players.getLocal().getLocation());
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
    }, TEST_INPUT(KeyEvent.VK_K) {
        @Override
        public void onActivation() {
            for (char c : "13".toCharArray())
                Inubot.getInstance().getCanvas().sendKey(c, 20);
            Inubot.getInstance().getCanvas().pressEnter();
        }
    }, OPEN_AMYLASE_PACKS(KeyEvent.VK_O) {
        @Override
        public void onActivation() {
            Inventory.apply(new NameFilter<>("Amylase pack"), item -> item.processAction("Open"));
        }
    }, OPEN_NEAREST_BANK_OBJECT(KeyEvent.VK_B) {
        @Override
        public void onActivation() {
            Processable p = GameObjects.getNearest(GameObject.Landmark.BANK);
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
                    Inubot.getInstance().getClient().getWidgets()[149][0].getStackSizes()[0] = 1284765393;
                    Inubot.getInstance().getClient().getWidgets()[149][0].getItemIds()[1] = 11804;
                    Inubot.getInstance().getClient().getWidgets()[149][0].getStackSizes()[1] = 128;
                    Inubot.getInstance().getClient().getWidgets()[149][0].getItemIds()[2] = 12819;
                    Inubot.getInstance().getClient().getWidgets()[149][0].getStackSizes()[2] = 12;
                    //Inubot.getInstance().getClient().getFont_p12full().drawString("Penis", 50, 50);
                    //Inubot.getInstance().getClient().drawRectangle(400, 300, 200, 200, Color.RED.getRGB());
                }
            }).start();
        }
    }, CREATE_ACCOUNT(KeyEvent.VK_N) {
        @Override
        public void onActivation() {
            new CreaterGUI().setVisible(true);
        }
    }, SHOW_WIDGET(KeyEvent.VK_G) {
        @Override
        public void onActivation() {
            new WidgetExplorer().setVisible(true);
        }
    };

    private final int key;

    private Hotkey(int key) {
        this.key = key;
    }

    public abstract void onActivation();

    public int getKey() {
        return key;
    }
}
