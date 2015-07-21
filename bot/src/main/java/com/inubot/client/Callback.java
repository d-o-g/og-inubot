package com.inubot.client;

import com.inubot.Bot;
import com.inubot.Inubot;
import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.oldschool.event.*;
import com.inubot.api.util.Time;
import com.inubot.script.Script;
import com.inubot.script.Task;

public class Callback {

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        if (!action.equals("Cancel")) {
            System.out.println(arg1 + "," + arg2 + "," + op + "," + arg0 + "," + action + "," + target);
            System.out.println("" + Action.valueOf(op, arg0, arg1, arg2));
        }
    }

    @ClientInvoked
    public static void experienceGain(int index, int experience) {
        if (Skill.values().length <= index || !Game.isLoggedIn()) {
            return;
        }
        System.out.println(Skills.getExperience(Skill.values()[index]) + " -> " + experience);
        Script script = Bot.getInstance().getScriptFlux().getRunning();
        if (script != null && script instanceof ExperienceListener) {
            ((ExperienceListener) script).experienceChanged(new ExperienceEvent(index, Skills.getExperience(Skill.values()[index]), experience));
        }
    }

    @ClientInvoked
    public static void messageReceived(int type, String sender, String message, String channel) {
        Script script = Bot.getInstance().getScriptFlux().getRunning();
        if (script != null && script.isRunning()) {
            script.messageReceived(new MessageEvent(sender, message, channel, type));
        }
    }

    @ClientInvoked
    public static void onEngineTick() {
        Client.processActions();
        if (Client.GAME_TICK_SLEEP != -1)
            Time.sleep(Client.GAME_TICK_SLEEP);
        if (Bot.getInstance() == null)
            return;
        Script script = Bot.getInstance().getScriptFlux().getRunning();
        if (script == null || script.isPaused())
            return;
        script.getTickTasks().forEach(Task::execute);
    }
}
