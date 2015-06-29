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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class AutoFisherPRO extends Script implements Paintable {

    private static final Logger logger = LoggerFactory.getLogger(AutoFisherPRO.class);
    private static final int[] EQUIPMENT_IDS = {305, 303, 301, 307, 309, 311};
    private static final String[] FISH_NAMES = {"Shrimps, Anchovies", "Shark",
            "Bass, Cod, Herring", "Lobster", "Tuna, Swordfish"};

    private boolean closed, powerfish;
    private int startExp, startLvl, caught;
    private Location location;
    private Fish fish;
    private Npc spot;

    private final Action[] actions = {new Dropping(), new Fishing(), new Depositing(), new Walking()};

    @Override
    public boolean setup() {
        EventQueue.invokeLater(GUI::new);
        try {
            while (!closed) {
                Time.sleep(700);
            }
        } catch (final Throwable ignored) {
        }
        startExp = Skills.getExperience(Skill.FISHING);
        startLvl = Skills.getLevel(Skill.FISHING); //maybe do this in loop? might not start logged in
        return true;
    }

    @Override
    public int loop() {
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
        FISHING_GUILD(new Tile(2588, 3419, 0), new Tile(2600, 3422));

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
            final JComboBox<String> options = new JComboBox<>(FISH_NAMES);
            final JComboBox<Location> cath = new JComboBox<>(Location.values());
            final JCheckBox power = new JCheckBox("Powerfish");
            final JButton start = new JButton("start");
            start.addActionListener(e -> {
                setVisible(false);
                closed = true;
                powerfish = power.isSelected();
                location = Location.values()[cath.getSelectedIndex()];
                int index = options.getSelectedIndex();
                fish = Fish.values()[index];
                logger.info("You are " + (powerfish ? "power" : "") + "fishing "
                        + FISH_NAMES[index]);
                logger.info(FISH_NAMES[index] + " spot has actions "
                        + fish.action + " and "
                        + fish.otherAction);
            });
            add(options);
            add(cath);
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
            if (spot != null && (Players.getLocal().getAnimation() == -1 || Players.getLocal().getTarget() == null)) {
                spot.processAction(fish.action);
                Time.await(() -> Players.getLocal().getAnimation() != -1, 5000);
            } else {
                setSpot();
            }
        }

        @Override
        public boolean validate() {
            setSpot();
            return !Inventory.isFull() && Players.getLocal().getAnimation() == -1;
        }

        private void setSpot() {
            if (spot != null) {
                if (Npcs.getNearest(n -> n.getLocation().equals(spot.getLocation())) == null) {
                    spot = null;
                } else {
                    List<String> actions = Arrays.asList(spot.getDefinition().getActions());
                    if (!actions.contains(fish.action) && !actions.contains(fish.otherAction)) {
                        spot = null;
                    }
                }
            } else {
                for (Npc spot0 : Npcs.getLoaded()) {
                    if (spot0 != null && spot0.getDefinition() != null) {
                        List<String> actions = Arrays.asList(spot0.getDefinition().getActions());
                        if (actions.contains(fish.action) && actions.contains(fish.otherAction)) {
                            spot = spot0;
                            break;
                        }
                    }
                }
            }
        }
    }

    private class Walking implements Action {

        @Override
        public void execute() {
            if (canWalkToBank()) {
                Movement.walkTo(location.bank);
            } else if (canWalkToFish()) {
                Movement.walkTo(location.spot);
            } else if (spot != null) {
                Movement.walkTo(spot.getLocation());
            }
        }

        @Override
        public boolean validate() {
            return canWalkToBank() || (spot != null && canWalkToFish());
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
            return !powerfish && (Bank.isOpen() && Inventory.isFull()
                    || Inventory.isFull() && bank != null && bank.distance() < 5);
        }
    }
}
