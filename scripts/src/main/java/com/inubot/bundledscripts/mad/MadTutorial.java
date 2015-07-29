package com.inubot.bundledscripts.mad;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Varps;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Random;
import com.inubot.bundledscripts.mad.modules.*;
import com.inubot.bundledscripts.mad.util.wrappers.ModuleWrapper;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.awt.*;

/**
 * Created by mad on 7/25/15.
 */
@Manifest(name = "MadTutorial", developer = "Mad", desc = "Fast tutorial island solver.", version = 1.0)
public class MadTutorial extends Script implements Paintable {

    private final ModuleWrapper mw = new ModuleWrapper();

    @Override
    public boolean setup() {
        mw.addModuleList(() -> Varps.get(281) < 1000);
        mw.addModuleList(() -> Varps.get(281) >= 1000);

        mw.submit(new RSGuide(), 0);
        mw.submit(new SurvivalExpert(), 0);
        mw.submit(new Chef(), 0);
        mw.submit(new QuestGuide(), 0);
        mw.submit(new Miner(), 0);
        mw.submit(new Combat(), 0);
        mw.submit(new Bank(), 0);
        mw.submit(new Monk(), 0);
        mw.submit(new Magic(), 0);
        return true;
    }

    @Override
    public int loop() {
        if(Game.isLoggedIn()) {
            mw.execute();
        }

        if(Varps.get(281) >= 1000) stop();

        return Random.nextInt(260,420);
    }

    @Override
    public void render(Graphics2D graphics2D) {
        graphics2D.drawString("MadTutorial", 100, 100);
        graphics2D.drawString("Status: " + mw.getValidModule().get().getClass().getSimpleName(), 100, 115);
        graphics2D.drawString("Setting: " + Varps.get(281), 100, 130);
    }
}
