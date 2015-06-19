/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes.tutisland.dogerina;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Widget;
import com.inubot.script.Script;
import com.inubot.script.Script;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author unsigned
 * @since 30-04-2015
 */
public class PerfectTutorialIsland extends Script {

    private static final int STAGE_VARP = 406;
    private static final int STEP_VARP = 281;

    private static boolean invalidateWidget(String text) {
        return Interfaces.getWidgetByText(s -> s.equals(text)) == null;
    }

    @Override
    public int loop() {
        if (Interfaces.canContinue())
            Interfaces.clickContinue();
        Stage stage = Stage.get();
        if (stage == null)
            throw new InternalError("Unrecognized stage: " + Varps.get(STAGE_VARP));
        Step step = stage.getCurrent();
        switch (step) {
            case CUSTOMIZING_CHARACTER:
                Widget acceptMakeover = Interfaces.getWidgetByText(s -> s.equals("Accept"));
                if (acceptMakeover != null) {
                    acceptMakeover.processAction("Accept");
                    step.complete.set(true); //since interaction can't fail, there's no other validation required so it's safe to assume
                }
                break;
            case TALKING_TO_RUNESCAPE_GUIDE:
                Npc guide = Npcs.getNearest("RuneScape Guide");
                if (guide != null) {
                    guide.processAction("Talk-to");
                    step.complete.set(true);
                }
                break;
            case OPENING_OPTIONS_TAB:
                Tabs.open(Tab.OPTIONS);
                step.complete.set(true);
                break;
            case OPENING_START_DOOR:
                GameObject exit = GameObjects.getNearest("Door");
                if (exit != null) {
                    exit.processAction("Open");
                    step.complete.set(true); //TODO perhaps some extra validation here
                }
                break;
        }
        return 500;
    }

    private enum Stage {

        MAKEOVER(0, Step.CUSTOMIZING_CHARACTER),
        RUNESCAPE_GUIDE(10, Step.TALKING_TO_RUNESCAPE_GUIDE, Step.OPENING_START_DOOR, Step.OPENING_OPTIONS_TAB);

        private final int varpState; //the max varp state
        private final Step[] steps;

        private Stage(int varpState, Step... steps) {
            this.varpState = varpState;
            this.steps = steps;
        }

        public static Stage get() {
            for (Stage stage : values()) {
                if (stage.varpState <= Varps.get(STAGE_VARP))
                    return stage;
            }
            return null;
        }

        public Step getCurrent() {
            for (Step step : steps) {
                if (step.complete.get() || !step.isActive())
                    continue;
                return step;
            }
            return null;
        }
    }

    private enum Step {

        CUSTOMIZING_CHARACTER {
            @Override
            public boolean isActive() {
                return !invalidateWidget("Use the buttons below to design your player");
            }
        },

        TALKING_TO_RUNESCAPE_GUIDE {
            @Override
            public boolean isActive() {
                return invalidateWidget("Use the buttons below to design your player")
                        && (Varps.get(STEP_VARP) == 0 || Varps.get(STEP_VARP) == 7);
            }
        },

        OPENING_START_DOOR {
            @Override
            public boolean isActive() {
                return invalidateWidget("Use the buttons below to design your player") && Varps.get(STEP_VARP) == 10;
            }
        },

        OPENING_OPTIONS_TAB {
            @Override
            public boolean isActive() {
                return invalidateWidget("Use the buttons below to design your player") && Varps.get(STEP_VARP) == 3;
            }
        };

        private final AtomicBoolean complete = new AtomicBoolean();

        public abstract boolean isActive();
    }
}
