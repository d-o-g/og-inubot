package com.inubot.incomplete.septron;

import com.inubot.api.oldschool.Tile;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

/**
 * @author Septron
 * @since May 05, 2017
 */
@Manifest(name = "sssss", developer = "septron", desc = "emn")
public class RimmingtonMiner extends Script {

    private static final Tile BANK_LOC = new Tile(3045, 3235);
    private static final Tile START_LOC = new Tile(2969, 3239);

    @Override
    public int loop() {
        return 0;
    }
}
