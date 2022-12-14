/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.PathingEntity;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.*;

public class Magic {

    public static boolean canAutoCast() {
        return Varps.get(843) != 0;
    }

    public static boolean isAutoCasting() {
        return Varps.get(108) != 0;
    }

    public static boolean isDefensiveCasting() {
        return Varps.get(439) >>> 8 == 1;
    }

    public static boolean isCastable(Spell spell) {
        return Skills.getLevel(Skill.MAGIC) >= spell.getLevel(); //TODO implement rune/quest requirements and check that we have runes
    }

    public static boolean select(Spell spell) {
        if (spell.toString().toLowerCase().contains("teleport")) {
            InterfaceComponent interfaceComponent = spell.getComponent();
            interfaceComponent.processAction("Cast", spell.toString());
            return true;
        } else {
            InterfaceComponent interfaceComponent = spell.getComponent();
            if (interfaceComponent != null) {
                Client.processAction(new SelectableSpellButtonAction(interfaceComponent.getId()), "Cast", spell.toString());
                return true;
            }
        }
        return false;
    }

    public static boolean cast(Spell spell, PathingEntity pathingEntity, String action) {
        if (spell == null || pathingEntity == null || action == null) {
            return false;
        }
        Client.processAction(pathingEntity instanceof Npc ? new SpellOnNpc(ActionOpcodes.SPELL_ON_NPC, pathingEntity.getArrayIndex())
                        : new SpellOnPlayer(ActionOpcodes.SPELL_ON_PLAYER, pathingEntity.getArrayIndex()),
                action, spell.toString());
        return true;
    }

    public static boolean cast(Spell spell, PathingEntity pathingEntity) {
        return cast(spell, pathingEntity, "Cast");
    }

    public static boolean cast(Spell spell, Item item, String action) {
        if (item == null || action == null) {
            return false;
        }
        Client.processAction(new SpellOnItemAction(item.getId(), item.getIndex(), item.getOwner().getRaw().getId()), action, item.getName());
        return true;
    }

    public static boolean cast(Spell spell, Item item) {
        return cast(spell, item, "Cast");
    }

    public static boolean cast(Spell spell, GroundItem item, String action) {
        if (spell == null || item == null || action == null) {
            return false;
        }
        Client.processAction(new SpellOnEntityAction(ActionOpcodes.SPELL_ON_GROUND_ITEM, item.getId(),
                item.getRaw().getRegionX(), item.getRaw().getRegionY()), action, spell.toString());
        return true;
    }

    public static boolean cast(Spell spell, GroundItem item) {
        return cast(spell, item, "Cast");
    }

    public static boolean cast(Spell spell, GameObject obj, String action) {
        if (spell == null || obj == null || action == null) {
            return false;
        }
        Client.processAction(new SpellOnEntityAction(ActionOpcodes.SPELL_ON_OBJECT, obj.getRaw().getId(),
                obj.getRegionX(), obj.getRegionY()), action, spell.toString());
        return true;
    }

    public static boolean cast(Spell spell, GameObject obj) {
        return cast(spell, obj, "Cast");
    }

    public static boolean cast(Spell spell, InterfaceComponent target, String action) {
        if (spell == null || target == null || action == null) {
            return false;
        }
        Client.processAction(new SpellOnComponentAction(target.getIndex(), target.getId()), action, spell.toString());
        return true;
    }

    public static boolean cast(Spell spell, InterfaceComponent target) {
        return cast(spell, target, "Cast");
    }

    public static int getCurrentBook() {
        int[] books = {Spell.MODERN, Spell.ANCIENT, Spell.LUNAR};
        int index = Varps.get(439) + (Magic.isDefensiveCasting() ? -256 : 0);
        assert index >= 0 || index <= 2 : "Unknown spellbook? wat";
        return books[index];
    }

    public static boolean isSpellSelected() {
        return Game.getClient().isSpellSelected();
    }
}
