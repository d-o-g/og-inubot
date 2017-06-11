package com.inubot.client;

import com.inubot.Bot;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Game;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.oldschool.event.ExperienceEvent;
import com.inubot.api.oldschool.event.ExperienceListener;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.util.Time;
import com.inubot.client.natives.oldschool.RSScriptEvent;
import com.inubot.script.Script;
import com.inubot.script.Task;

import java.awt.*;
import java.util.Arrays;

public class Callback {

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        if (!action.equals("Cancel")) {
            System.out.println(op + "," + arg0 + "," + arg1 + "," + arg2 + "," + action + "," + target);
            System.out.println(Action.valueOf(op, arg0, arg1, arg2));
        }
    }

    @ClientInvoked
    public static void experienceGain(int index, int experience) {
        if (Skill.values().length <= index || !Game.isLoggedIn() || experience <= 0) {
            return;
        }
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
    public static void draw(Image image) {
        Graphics g = image.getGraphics().create();
        Game.getCanvas().paintables.forEach(p -> p.render(((Graphics2D) g)));
    }

    @ClientInvoked
    public static void script(RSScriptEvent e, int size) {

    }

    @ClientInvoked
    public static void onEngineTick() {
        synchronized (Time.getCycleLock()) {
            Time.getCycleLock().notifyAll();
        }
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
