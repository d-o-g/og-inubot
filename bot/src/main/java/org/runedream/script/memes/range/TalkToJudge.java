/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.range;

import org.runedream.api.methods.Interfaces;
import org.runedream.api.methods.Npcs;
import org.runedream.api.oldschool.Npc;
import org.runedream.script.memes.Action;

/**
 * @author unsigned
 * @since 25-04-2015
 */
public class TalkToJudge implements Action {
    @Override
    public boolean validate() {
        return (RangeGuild.isGameFinished() || !RangeGuild.isGameStarted()) && Interfaces.getWidgets(RangeGuild.DIALOGUE_FILTER).length == 0;
    }

    @Override
    public void execute() {
        Npc npc = Npcs.getNearest("Competition Judge");
        if (npc != null) {
            npc.processAction("Talk-to");
        }
    }
}
