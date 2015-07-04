/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.apis.methods.traversal.graph.data;

import com.inubot.apis.oldschool.Tile;
import com.inubot.apis.util.filter.Filter;

public enum WebBank {

    /**
     * ***********************************Bank booths*************************************
     */
    LUMBRIDGE_CASTLE(new Tile(3208, 3220, 2)),
    FALADOR_WEST(new Tile(2947, 3368)),
    FALADOR_EAST(new Tile(3013, 3355)),
    VARROCK_WEST(new Tile(3185, 3436)),
    VARROCK_EAST(new Tile(3253, 3420)),
    EDGEVILLE(new Tile(3094, 3491)),
    DRAYNOR(new Tile(3092, 3245)),
    AL_KHARID(new Tile(3269, 3167)),
    SEERS_VILLAGE(new Tile(2727, 3493)),
    CATHERBY(new Tile(2809, 3441)),
    YANILLE(new Tile(2613, 3094)),
    ARDOUGNE_NORTH(new Tile(2615, 3332)),
    ARDOUGNE_SOUTH(new Tile(2655, 3283)),
    FISHING_GUILD(new Tile(2586, 3419)),
    CANIFIS(new Tile(3512, 3480)),
    /**
     * ***********************************Bank chests*************************************
     */
    SHANTAY_PASS("Bank chest", "Use", new Tile(3309, 3120), Type.BANK_CHEST),
    CASTLE_WARS("Bank chest", "Use", new Tile(2444, 3083), Type.BANK_CHEST),
    /**
     * ************************************Bank Npcs**************************************
     */
    MISCELLANIA("Banker", "Bank", new Tile(2618, 3895), Type.NPC),
    FIGHT_CAVES("TzHaar-Ket-Zuh", "Bank", new Tile(2446, 5178), Type.NPC),
    BURTHORPE("Emerald Benedicht", "Bank", new Tile(3047, 4974, 1), Type.NPC),
    /**
     * ********************************Bank deposit boxs**********************************
     */
    PORT_SARIM_DOCKS("Bank deposit box", "Deposit", new Tile(3045, 3235), Type.DEPOSIT_BOX);

    private final String name, action;
    private final Tile tile;
    private final Type type;

    private WebBank(String name, String action, Tile tile, Type type) {
        this.name = name;
        this.action = action;
        this.tile = tile;
        this.type = type;
    }

    private WebBank(String name, String action, Tile tile) {
        this(name, action, tile, Type.BANK_BOOTH);
    }

    private WebBank(Tile tile) {
        this("Bank booth", "Bank", tile, Type.BANK_BOOTH);
    }

    public static WebBank getNearest(Filter<WebBank> filter) {
        int dist = Integer.MAX_VALUE;
        WebBank ret = null;
        for (WebBank bank : WebBank.values()) {
            if ((filter == null || filter.accept(bank)) && bank.getLocation().distance() < dist) {
                ret = bank;
                dist = bank.getLocation().distance();
            }
        }
        return ret;
    }

    public static WebBank getNearestWithdrawable() {
        return getNearest(nexusBank -> nexusBank.getType() != Type.DEPOSIT_BOX);
    }

    public static WebBank getNearestDepositBox() {
        return getNearest(nexusBank -> nexusBank.getType() == Type.DEPOSIT_BOX);
    }

    public static WebBank getNearestChest() {
        return getNearest(nexusBank -> nexusBank.getType() == Type.BANK_CHEST);
    }

    public static WebBank getNearestBooth() {
        return getNearest(nexusBank -> nexusBank.getType() == Type.BANK_BOOTH);
    }

    public static WebBank getNearestNpc() {
        return getNearest(nexusBank -> nexusBank.getType() == Type.NPC);
    }

    public static WebBank getNearest() {
        return getNearest(null);
    }

    public Tile getLocation() {
        return tile;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAction() {
        return action;
    }

    public static enum Type {
        NPC, DEPOSIT_BOX, BANK_CHEST, BANK_BOOTH
    }
}

