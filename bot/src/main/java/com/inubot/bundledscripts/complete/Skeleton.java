package com.inubot.bundledscripts.complete;

import com.inubot.api.oldschool.event.*;
import com.inubot.api.util.*;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.awt.*;

@Manifest(name = "", developer = "", desc = "")
public class Skeleton extends Script implements Paintable, ExperienceListener {

    private StopWatch stopWatch;
    private int experienceGained;

    @Override
    public boolean setup() {
        this.stopWatch = new StopWatch(0);
        return true;
    }

    @Override
    public int loop() {
        return Random.nextInt(700, 900);
    }

    @Override
    public void experienceChanged(ExperienceEvent e) {
        experienceGained += e.getGain();
    }

    @Override
    public void messageReceived(MessageEvent e) {

    }

    @Override
    public void render(Graphics2D g) {
        g.drawString("Hello world!", 20, 20);
        g.drawString("Runtime: " + stopWatch.toElapsedString(), 20, 40);
        g.drawString("Experience: " + experienceGained, 20, 60);
    }
}
