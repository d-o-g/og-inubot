/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.zulrah;

import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Prayers;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Prayer;
import com.inubot.script.Manifest;
import com.inubot.script.bundled.proscripts.framework.ProScript;

import java.util.Map;

/**
 * @author Dogerina
 * @since 17-07-2015
 */
@Manifest(
        name = "Pro Zulrah",
        developer = "blitz",
        desc = "Kills Zulrah for good cash money"
)
public class ProZulrah extends ProScript implements ZulrahConstants {

    public ProZulrah() {
        super.setForceIdleTimeClick(false);
        super.setPaintHidden(true);
    }

    @Override
    public void getPaintData(Map<String, Object> data) {

    }

    @Override
    public int loop() {
        Npc npc = Npcs.getNearest("Zulrah");
        if (npc != null) {
            System.out.println(npc.getAnimation());
            if (npc.getId() == 2042 || npc.getAnimation() == 2042) {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            } else if (npc.getId() == 2044 || npc.getAnimation() == 2044) {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            } else if (npc.getId() == 2043 || npc.getAnimation() == 2043) {
                Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            }
        }
        return 1000;
    }
}
