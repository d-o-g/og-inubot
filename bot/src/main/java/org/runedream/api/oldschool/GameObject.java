/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool;

import org.runedream.api.methods.*;
import org.runedream.api.oldschool.action.*;
import org.runedream.api.oldschool.action.tree.Action;
import org.runedream.api.util.CacheLoader;
import org.runedream.api.util.Identifiable;
import org.runedream.client.natives.RSGameObject;
import org.runedream.client.natives.RSObjectDefinition;

import java.util.Arrays;

/**
 * @author unsigned
 * @since 23-04-2015
 */
public class GameObject extends Wrapper<RSGameObject> implements Locatable, Processable, Identifiable {

    private final RSObjectDefinition definition;

    public GameObject(RSGameObject raw) {
        super(raw);
        this.definition = CacheLoader.findObjectDefinition(UID.getEntityId(raw.getId()));
    }

    public int getRegionX() {
        return raw.getId() & 0x7f;
    }

    public int getRegionY() {
        return raw.getId() >> 7 & 0x7f;
    }

    public int getStrictX() {
        return raw.getX() << 7;
    }

    public int getStrictY() {
        return raw.getY() << 7;
    }

    public int getX() {
        return getRegionX() + Game.getRegionBaseX();
    }

    public int getY() {
        return getRegionY() + Game.getRegionBaseY();
    }

    public int getId() {
        return UID.getEntityId(raw.getId());
    }

    public RSObjectDefinition getDefinition() {
        return definition;
    }

    @Override
    public Tile getLocation() {
        return new Tile(getX(), getY());
    }

    @Override
    public int distance(Locatable locatable) {
        return (int) Projection.distance(this, locatable);
    }

    @Override
    public int distance() {
        return distance(Players.getLocal());
    }

    @Override
    public void processAction(int opcode, String action) {
        RSObjectDefinition definition = getDefinition();
        if (definition == null)
            return;
        String name = definition.getName();
        if (name == null)
            return;

        // if shit breaks look here
        //Client.processAction(Action.valueOf(opcode, raw.getId(), raw.getX(), raw.getY()), action, name);
        Client.processAction(Action.valueOf(opcode, raw.getId(), getRegionX(), getRegionY()), action, name);
    }

    public void processAction(String action) {
        RSObjectDefinition definition = getDefinition();
        if (definition == null)
            return;
        String[] actions = definition.getActions();
        if (actions == null)
            return;
        int index = Arrays.asList(actions).indexOf(action);
        if (index >= 0)
            processAction(ActionOpcodes.OBJECT_ACTION_0 + index, action);
    }

    public String getName() {
        RSObjectDefinition def = getDefinition();
        return def == null ? null : def.getName();
    }

    public boolean containsAction(String act) {
        for (String action : getActions()) {
            if (action == null) continue;
            if (action.equals(act)) {
                return true;
            }
        }
        return false;
    }

    public String[] getActions() {
        return getDefinition() == null ? new String[0] : getDefinition().getActions();
    }

    public enum Landmark {

        GENERAL_STORE(0),
        SWORD_SHOP(1),
        MAGIC_SHOP(2),
        AXE_SHOP(3),
        HELMET_SHOP(4),
        BANK(5),
        QUEST_START(6),
        AMULET_SHOP(7),
        MINING_SITE(8),
        FURNACE(9),
        ANVIL(10),
        COMBAT_TRAINING(11),
        DUNGEON(12),
        STAFF_SHOP(13),
        PLATEBODY_SHOP(14),
        PLATELEGS_SHOP(15),
        SCIMITAR_SHOP(16),
        ARCHERY_SHOP(17),
        SHIELD_SHOP(18),
        ALTAR(19),
        HERBALIST(20),
        JEWELLERY(21),
        GEM_SHOP(22),
        CRAFTING_SHOP(23),
        CANDLE_SHOP(24),
        FISHING_SHOP(25),
        FISHING_SPOT(26),
        CLOTHES(27),
        APOTHECARY(28),
        SILK_TRADER(29),
        KEBAB_SELLER(30),
        BAR(31),
        MACE_SHOP(32),
        TANNERY(33),
        RARE_TREES(34),
        SPINNING_WHEEL(35),
        FISH_SHOP(36),
        COOKERY_SHOP(37),
        MINIGAME(38),
        WATER_SOURCE(39),
        COOKING_RANGE(40),
        SKIRT_SHOP(41),
        POTTERS_WHEEL(42),
        WINDMILL(43),
        MINING_SHOP(44),
        CHAINMAIL_SHOP(45),
        SILVER_SHOP(46),
        FUR_TRADER(47),
        SPICE_SHOP(48),
        AGILITY_TRAINING(49),
        VEGETABLE_STORE(50),
        SLAYER_MASTER(51),
        HAIR_DRESSERS(52),
        FARMING_PATCH(53),
        MAKEOVER_MAGE(54),
        GUIDE(55),
        TRANSPORTATION(56),
        POH_PORTAL(57),
        FARMING_SHOP(58),
        LOOM(59),
        BREWERY(60),
        DAIRY_CHURN(61),
        ESTATE_AGENT(62),
        SAW_MILL(63),
        AGILITY_SHORTCUT(64),
        HOLIDAY_EVENTS(65),
        BUTTER_CHURN(66),
        ACHIEVEMENT_DIARY_STARTER(67),
        GRAND_EXCHANGE(76);

        private final int id;

        private Landmark(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public Landmark getLandmark() {
        if (definition == null)
            return null;
        for (Landmark landmark : Landmark.values()) {
            if (landmark.id == definition.getMapFunction())
                return landmark;
        }
        return null;
    }
}
