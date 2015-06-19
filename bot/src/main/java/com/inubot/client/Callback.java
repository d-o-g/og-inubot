package com.inubot.client;

import com.inubot.Inubot;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.util.Time;
import com.inubot.script.Script;
import com.inubot.script.Task;

import java.io.IOException;
import java.util.Objects;

public class Callback {

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        if (!action.equals("Cancel")) {
            System.out.println(Action.valueOf(op, arg0, arg1, arg2));
            System.out.println(arg1 + "," + arg2 + "," + op + "," + arg0 + "," + action + "," + target);
        }
    }

    @ClientInvoked
    public static void messageReceived(int type, String sender, String message, String channel) {
        Script script = Inubot.getInstance().getScriptFlux().getRunning();
        if (script != null && script.isRunning()) {
            script.messageReceived(type, sender, message, channel);
            if (type == 0) {
                try {
                    if (message.contains("Congratulations")) {
                        String a = message.replace("Congratulations, you just advanced a ", "").replace(" level.", "");
                        int level = 0;
                        for (Skill skill : Skill.values()) {
                            if (a.equalsIgnoreCase(skill.name()))
                                level = Skills.getLevel(skill);
                        }

                        Inubot.getInstance().getConnection().message("I am now level " + level + " in " + a);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!sender.equals("")) {
                try {
                    Inubot.getInstance().getConnection().message(sender + ": " + message + " (" + type + ")");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @ClientInvoked
    public static void onEngineTick() {
        Client.processActions();
        if (Client.GAME_TICK_SLEEP != -1)
            Time.sleep(Client.GAME_TICK_SLEEP);
        Script script = Inubot.getInstance().getScriptFlux().getRunning();
        if (script == null || script.isPaused())
            return;
        script.getTickTasks().forEach(Task::execute);
    }
}
