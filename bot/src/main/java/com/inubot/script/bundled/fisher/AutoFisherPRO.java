/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.fisher;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.script.Script;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class AutoFisherPRO extends Script implements Paintable {

    private static final int[] EQUIPMENT_IDS = {305, 303, 301, 307, 309, 311};
    private static final String[] FISH_NAMES = {"Shrimps, Anchovies", "Shark",
            "Bass, Cod, Herring", "Lobster", "Tuna, Swordfish"};

    private static final Tile BAD_TILE = new Tile(3246, 3157, 0); //hacky fix

    private boolean closed, powerfish;
    private int startExp, startLvl, caught;
    private Location location;
    private Fish fish;
    private Tile start;

    private Action[] actions;

    @Override
    public boolean setup() {
        EventQueue.invokeLater(GUI::new);
        try {
            while (!closed) {
                Time.sleep(700);
            }
        } catch (Throwable ignored) {
        }
        startExp = Skills.getExperience(Skill.FISHING);
        startLvl = Skills.getLevel(Skill.FISHING); //maybe do this in loop? might not start logged in
        if (!powerfish) {
            actions = new Action[]{new Dropping(), new Fishing(), new Depositing(), new Walking()};
        } else {
            actions = new Action[]{new Dropping(), new Fishing()};
        }
        return true;
    }

    @Override
    public int loop() {
        if (powerfish) {
            if (start == null) {
                start = Players.getLocal().getLocation();
            } else if (start.distance() > 20) {
                Movement.walkTo(start);
                return 2000;
            }
        }
        if (Interfaces.canContinue())
            Interfaces.processContinue();
        for (Action action : actions) {
            if (action.validate())
                action.execute();
        }
        return 800;
    }

    @Override
    public void render(Graphics2D g) {

    }

    @Override
    public void messageReceived(MessageEvent e) {
        if (e.getType() == MessageEvent.Type.SERVER && e.getText().contains("You catch")) {
            caught++;
        }
    }

    private enum Fish {

        SHRIMPS("Net", "Bait", "Small fishing net"),
        SHARK("Harpoon", "Net", "Harpoon"),
        BASS_COD("Net", "Harpoon", "Big fishing net"),
        LOBBIES("Cage", "Harpoon", "Lobster pot"),
        TUNA_SWORDIES("Harpoon", "Cage", "Harpoon");

        /**
         * ids change and names are all the same ("Fishing spot") but fishing spots have 2
         * actions for the spot e.g. shrimsp will always be net and bait, bass
         * will always be net and harpoon
         */
        private final String action;
        private final String otherAction;
        private final String equipment;

        private Fish(String action, String otherAction, String equipment) {
            this.action = action;
            this.otherAction = otherAction;
            this.equipment = equipment;
        }
    }

    private enum Location {

        CATHERBY(new Tile(2809, 3440, 0), new Tile(2852, 3424)),
        FISHING_GUILD(new Tile(2586, 3418, 0), new Tile(2600, 3422));

        private final Tile bank;
        private final Tile spot;

        private Location(Tile bank, Tile spot) {
            this.bank = bank;
            this.spot = spot;
        }

        @Override
        public String toString() {
            String name = super.name();
            return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
        }
    }

    private interface Action {
        boolean validate();

        void execute();
    }

    private class GUI extends JFrame {

        private GUI() {
            super("AutoFisherPRO");
            setLayout(new GridLayout(0, 2));
            JComboBox<String> options = new JComboBox<>(FISH_NAMES);
            JComboBox<Location> loc = new JComboBox<>(Location.values());
            JCheckBox power = new JCheckBox("Powerfish");
            JButton start = new JButton("start");
            start.addActionListener(e -> {
                setVisible(false);
                closed = true;
                powerfish = power.isSelected();
                location = Location.values()[loc.getSelectedIndex()];
                int index = options.getSelectedIndex();
                fish = Fish.values()[index];
                System.out.println("You are " + (powerfish ? "power" : "") + "fishing "
                        + FISH_NAMES[index]);
                System.out.println(FISH_NAMES[index] + " spot has actions "
                        + fish.action + " and "
                        + fish.otherAction);
            });
            power.addActionListener(e -> loc.setEnabled(!power.isSelected()));
            add(options);
            add(loc);
            add(power);
            add(start);
            pack();
            setVisible(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }
    }

    private class Dropping implements Action {

        @Override
        public void execute() {
            Inventory.dropAllExcept(new IdFilter<>(EQUIPMENT_IDS));
        }

        @Override
        public boolean validate() {
            return Inventory.isFull() && powerfish;
        }
    }

    private class Fishing implements Action {

        @Override
        public void execute() {
            Npc spot = Npcs.getNearest(spot0 -> {
                if (spot0.getLocation().equals(BAD_TILE))
                    return false;
                List<String> list = Arrays.asList(spot0.getDefinition().getActions());
                return list.contains(fish.action) && list.contains(fish.otherAction);
            });
            if (spot != null && (Players.getLocal().getAnimation() == -1 || Players.getLocal().getTarget() == null)) {
                spot.processAction(fish.action);
                Time.await(() -> Players.getLocal().getAnimation() != -1, 5000);
            }
        }

        @Override
        public boolean validate() {
            return !Inventory.isFull() && Players.getLocal().getAnimation() == -1;
        }
    }

    private class Walking implements Action {

        @Override
        public void execute() {
            if (canWalkToBank()) {
                Movement.walkTo(location.bank);
            } else if (canWalkToFish()) {
                Movement.walkTo(location.spot);
            }
        }

        @Override
        public boolean validate() {
            return canWalkToBank() || canWalkToFish();
        }

        private boolean canWalkToBank() {
            return Inventory.isFull() && location.bank.distance() > 7 && !powerfish;
        }

        private boolean canWalkToFish() {
            return !Inventory.isFull() && Players.getLocal().getAnimation() == -1 && location.spot.distance() > 7;
        }
    }

    private class Depositing implements Action {

        @Override
        public void execute() {
            if (Bank.isOpen() && Inventory.getCount() > 3) {
                Bank.depositAllExcept(new IdFilter<>(EQUIPMENT_IDS));
                Bank.close();
            } else {
                Bank.open();
            }
        }

        @Override
        public boolean validate() {
            GameObject bank = GameObjects.getNearest("Bank booth");
            return !powerfish && !Players.getLocal().isMoving() && (Bank.isOpen() && Inventory.isFull()
                    || Inventory.isFull() && bank != null && bank.distance() < 5);
        }
    }
}
