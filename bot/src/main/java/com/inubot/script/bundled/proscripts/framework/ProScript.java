/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.framework;

import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.event.ExperienceEvent;
import com.inubot.api.oldschool.event.ExperienceListener;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.script.Script;

import java.awt.*;
import java.util.*;

/**
 * @author Dogerina
 * @since 16-07-2015
 */
public abstract class ProScript extends Script implements Paintable, ExperienceListener {

    private static final int BASE_PAINT = 6;
    private static final int HEIGHT = 20;

    private final Map<Skill, TrackedSkill> trackedSkills;
    private final Map<String, Object> paintData;
    private final StopWatch stopWatch;

    public ProScript() {
        this.paintData = new LinkedHashMap<>();
        this.trackedSkills = new HashMap<>();
        this.stopWatch = new StopWatch(0);
    }

    public abstract String getTitle();

    public abstract void getPaintData(Map<String, Object> data);

    @Override
    public final void render(Graphics2D graphics) {
        paintData.put("Runtime", stopWatch.toElapsedString());
        for (TrackedSkill trackedSkill : trackedSkills.values()) {
            paintData.put(trackedSkill.skill.name().toLowerCase() + " experience", trackedSkill.gainedExperience);
            paintData.put(trackedSkill.skill.name().toLowerCase() + " experience/hr", stopWatch.getHourlyRate(trackedSkill.gainedExperience));
        }
        getPaintData(paintData);
        int widest = 0;
        for (Map.Entry<String, Object> entry : paintData.entrySet()) {
            String data = entry.getKey() + ": " + entry.getValue().toString();
            int width = graphics.getFontMetrics().stringWidth(data);
            if (width > widest) {
                widest = width;
            }
        }
        int dataLen = paintData.size() + 1;
        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(3.0f));
        graphics.drawRect(10, 10, widest + BASE_PAINT, BASE_PAINT + HEIGHT * dataLen);
        graphics.setColor(Color.BLACK);
        graphics.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        graphics.fillRect(11, 11, widest + BASE_PAINT - 1, BASE_PAINT + (HEIGHT * dataLen) - 1);
        graphics.setColor(Color.WHITE);
        graphics.drawString(getTitle(), 13, BASE_PAINT + HEIGHT);
        graphics.setColor(Color.GREEN);
        graphics.drawLine(12, 13 + HEIGHT, widest + BASE_PAINT + 8, 13 + HEIGHT);
        graphics.setColor(Color.WHITE.darker());
        int index = 2;
        for (Map.Entry<String, Object> entry : paintData.entrySet()) {
            String data = entry.getKey() + ": " + entry.getValue().toString();
            graphics.drawString(data, 13, BASE_PAINT + (HEIGHT * index));
            index++;
        }
    }

    @Override
    public final void experienceChanged(ExperienceEvent e) {
        if (!trackedSkills.containsKey(e.getSkill())) {
            trackedSkills.put(e.getSkill(), new TrackedSkill(e.getSkill()));
        }
        trackedSkills.get(e.getSkill()).increaseGainedExperience(e.getGain());
    }

    protected final TrackedSkill getTrackedSkill(Skill skill) {
        if (trackedSkills.containsKey(skill)) {
            return trackedSkills.get(skill);
        }
        return trackedSkills.put(skill, new TrackedSkill(skill));
    }

    protected class TrackedSkill {

        private final Skill skill;
        private int gainedExperience;

        private TrackedSkill(Skill skill) {
            this.skill = skill;
        }

        public int getGainedExperience() {
            return gainedExperience;
        }

        private void increaseGainedExperience(int increase) {
            gainedExperience += increase;
        }
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    //TODO maybe override onPause and onResume to stop/resume StopWatch?
}
