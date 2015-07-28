/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.bot.ui.CreaterGUI;
import com.inubot.bot.ui.WidgetExplorer;
import com.inubot.client.natives.oldschool.RSModel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

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
            System.out.println("Cache Built.... Listening");
            int[] vars = Inubot.getInstance().getClient().getVarps();
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
    }, DROP_INV(KeyEvent.VK_V) {
        @Override
        public void onActivation() {
            Inventory.dropAllExcept(new NameFilter<>("Coins"));
        }
    }, POSITION(KeyEvent.VK_E) {
        @Override
        public void onActivation() {
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
    }, OPEN_NEAREST_BANK_OBJECT(KeyEvent.VK_B) {
        @Override
        public void onActivation() {
            Bank.open();
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
    }, MELEE_SWITCH(KeyEvent.VK_C) {
        @Override
        public void onActivation() {
            for (String e : MELEE_EQUIPMENT) {
                WidgetItem item = Inventory.getFirst(t -> t.getName().startsWith(e));
                if (item != null) {
                    item.processAction(ActionOpcodes.ITEM_ACTION_1, "Wear");
                    item = Inventory.getFirst(e);
                    if (item != null)
                        item.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
                }
            }
        }
    }, MAGE_SWITCH(KeyEvent.VK_X) {
        @Override
        public void onActivation() {
            for (int i = 0; i != 4; i++) {
                WidgetItem item = Inventory.getItems()[i];
                if (item.getIndex() == i)
                    item.processAction(ActionOpcodes.ITEM_ACTION_1, "Wear");
            }
            for (String e : MAGIC_EQUIPMENT) {
                WidgetItem item = Inventory.getFirst(e);
                if (item != null) {
                    item.processAction(ActionOpcodes.ITEM_ACTION_1, "Wear");
                    item = Inventory.getFirst(e);
                    if (item != null)
                        item.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
                }
            }
        }
    }, SELECT_ICE_BARRAGE(KeyEvent.VK_Z) {
        @Override
        public void onActivation() {
            Client.processAction(0, -1, 14286917, 25, "Cast", "Ice Barrage", 50, 50);
        }
    };

    private static final String[] MELEE_EQUIPMENT = {
            "Bandos chestplate", "Bandos tassets", "Amulet of fury", "Serpentine helm", "Bandos boots",
            "Berserker ring (i)", "Dragonfire shield", "Abyssal tentacle", "Barrows gloves", "Fire cape"
    };

    private static final String[] MAGIC_EQUIPMENT = {
            "Ahrim's hood", "Ahrim's robetop", "Ahrim's robeskirt", "Seers ring (i)", "Infinity boots",
            "Barrows gloves", "Arcane spirit shield", "Toxic staff of the dead", "Occult necklace", "Zamorak cape"
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
