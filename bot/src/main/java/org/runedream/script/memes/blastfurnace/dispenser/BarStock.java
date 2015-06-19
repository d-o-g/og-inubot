/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.blastfurnace.dispenser;

import org.runedream.api.methods.Varps;
import org.runedream.client.natives.RSVarpBit;

/**
 * @author unsigned
 * @since 29-04-2015
 */
public class BarStock {

    public static final RSVarpBit BRONZE;
    public static final RSVarpBit IRON;
    public static final RSVarpBit STEEL;
    public static final RSVarpBit MITHRIL;
    public static final RSVarpBit ADAMANTITE;
    public static final RSVarpBit RUNITE;
    public static final RSVarpBit SILVER;
    public static final RSVarpBit GOLD;

    static {
        //Varp 545
        BRONZE = Varps.getBit(941);
        IRON = Varps.getBit(942);
        STEEL = Varps.getBit(943);
        MITHRIL = Varps.getBit(944);
        //-----------------------------
        //Varp 546
        ADAMANTITE = Varps.getBit(945);
        RUNITE = Varps.getBit(946);
        SILVER = Varps.getBit(947);
        GOLD = Varps.getBit(948);
        //-----------------------------

        verify(); //Verify everything was loaded OK
    }

    /*
     * The maximum number of any type of bar that can be stored within
     * the BarStock at any given time.
     */
    private static final int MAX_BARS = 255;

    private static void verify() {
        verify("Bronze", BRONZE);
        verify("Iron", IRON);
        verify("Steel", STEEL);
        verify("Mithril", MITHRIL);
        verify("Adamantite", ADAMANTITE);
        verify("Runite", RUNITE);
        verify("Silver", SILVER);
        verify("Gold", GOLD);
    }

    private static void verify(String name, RSVarpBit chk) {
        if (chk == null)
            throw new InternalError(name + " Varpbit == null");
    }
}
