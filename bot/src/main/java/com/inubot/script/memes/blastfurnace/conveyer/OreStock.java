/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes.blastfurnace.conveyer;

import com.inubot.api.methods.Varps;
import com.inubot.api.methods.Varps;
import com.inubot.client.natives.RSVarpBit;
import com.inubot.script.memes.blastfurnace.mechanics.MechanicUtils;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public class OreStock {

    public static final RSVarpBit TIN;
    public static final RSVarpBit COAL;
    public static final RSVarpBit COPPER;
    public static final RSVarpBit SILVER;
    public static final RSVarpBit MITHRIL;
    public static final RSVarpBit GOLD;
    public static final RSVarpBit IRON;
    public static final RSVarpBit ADAMANTITE;
    public static final RSVarpBit RUNITE;

    static {
        SILVER = Varps.getBit(948);
        COAL = Varps.getBit(949);
        TIN = Varps.getBit(950);
        IRON = Varps.getBit(951);
        MITHRIL = Varps.getBit(952);
        GOLD = Varps.getBit(955);
        COPPER = Varps.getBit(959);
        ADAMANTITE = Varps.getBit(945);
        RUNITE = null; //TODO need smithing level
        verify();
    }

    /*
     * The maximum account of any ore that can be stored within the
     * BarStock any given time.
     */
    private static final int MAX_ORE = 255;

    private static void verify() {
        MechanicUtils.verifyVarpbit("Tin", TIN, MAX_ORE);
        MechanicUtils.verifyVarpbit("Steel", COAL, MAX_ORE);
        MechanicUtils.verifyVarpbit("Copper", COPPER, MAX_ORE);
        MechanicUtils.verifyVarpbit("Silver", SILVER, MAX_ORE);
        MechanicUtils.verifyVarpbit("Mithril", MITHRIL, MAX_ORE);
        MechanicUtils.verifyVarpbit("Gold", GOLD, MAX_ORE);
        MechanicUtils.verifyVarpbit("Iron", IRON, MAX_ORE);
        MechanicUtils.verifyVarpbit("Adamantite", ADAMANTITE, MAX_ORE);
        //TODO ...Runite
    }
}
