package com.inubot.bundledscripts.proframework;

import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.event.ExperienceEvent;
import com.inubot.api.oldschool.event.ExperienceListener;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ProScript extends Script implements Paintable, ExperienceListener {

    private static final int BASE_PAINT = 6;
    private static final int HEIGHT = 20;

    private final Map<Skill, TrackedSkill> trackedSkills;
    private final Map<String, Object> paintData;
    private final StopWatch stopWatch;
    private boolean paintHidden = false;

    private Color textColor = Color.WHITE.darker(), lineColor = Color.GREEN;

    public ProScript() {
        this.paintData = new LinkedHashMap<>();
        this.trackedSkills = new HashMap<>();
        this.stopWatch = new StopWatch(0);
    }

    public String getTitle() {
        Manifest manifest = getClass().getAnnotation(Manifest.class);
        if (manifest == null) {
            return getClass().getSimpleName() + " v1.0";
        }
        return manifest.name() + " v" + manifest.version() + " by " + manifest.developer();
    }

    public abstract void getPaintData(Map<String, Object> data);

    @Override
    public final void render(Graphics2D graphics) {
        if (paintHidden) {
            return;
        }
        paintData.put("Runtime", stopWatch.toElapsedString());
        for (TrackedSkill trackedSkill : trackedSkills.values()) {
            if (trackedSkill.gainedExperience == 0) {
                continue;
            }
            paintData.put(trackedSkill.skill.toString() + " experience", trackedSkill.gainedExperience);
            paintData.put(trackedSkill.skill.toString() + " experience/hr", stopWatch.getHourlyRate(trackedSkill.gainedExperience));
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
        int titleWidth = graphics.getFontMetrics().stringWidth(getTitle());
        if (titleWidth > widest) {
            widest = titleWidth;
        }
        int dataLen = paintData.size() + 1;
        graphics.setColor(lineColor);
        graphics.setStroke(new BasicStroke(3.0f));
        graphics.drawRect(10, 10, widest + BASE_PAINT, BASE_PAINT + HEIGHT * dataLen);
        graphics.setColor(Color.BLACK);
        graphics.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        graphics.fillRect(11, 11, widest + BASE_PAINT - 1, BASE_PAINT + (HEIGHT * dataLen) - 1);
        AWTUtil.drawBoldedString(graphics, getTitle(), 13, BASE_PAINT + HEIGHT, textColor.brighter());
        graphics.setColor(lineColor);
        graphics.drawLine(12, 13 + HEIGHT, widest + BASE_PAINT + 8, 13 + HEIGHT);
        graphics.setColor(textColor);
        int index = 2;
        for (Map.Entry<String, Object> entry : paintData.entrySet()) {
            String data = entry.getKey() + ": " + entry.getValue().toString();
            AWTUtil.drawBoldedString(graphics, data, 13, BASE_PAINT + (HEIGHT * index));
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

    public boolean isPaintHidden() {
        return paintHidden;
    }

    public void setPaintHidden(boolean paintHidden) {
        this.paintHidden = paintHidden;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    @Override
    public void onPause() {
        stopWatch.pause();
    }

    @Override
    public void onResume() {
        stopWatch.resume();
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
}
