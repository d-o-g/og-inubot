package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Players;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

/**
 * Created by Asus on 09/05/2017.
 */
@Manifest(name = "Dogbugger", developer = "Dogerina", desc = "Debug basic information")
public class Dogbugger extends ProScript {
    @Override
    public int loop() {
        return 1000;
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        data.put("Animation", Players.getLocal().getAnimation());
        data.put("Position", Players.getLocal().getLocation());
        data.put("Connection state", Game.getState());
    }
}
