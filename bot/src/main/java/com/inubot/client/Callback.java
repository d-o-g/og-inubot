package com.inubot.client;

import com.inubot.Inubot;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.util.Time;
import com.inubot.script.Script;
import com.inubot.script.Task;

public class Callback {

    @ClientInvoked
    public static void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y) {
        if (!action.equals("Cancel")) {
            System.out.println("" + Action.valueOf(op, arg0, arg1, arg2));
        }
    }

    @ClientInvoked
    public static void messageReceived(int type, String sender, String message, String channel) {
        Script script = Inubot.getInstance().getScriptFlux().getRunning();
        if (script != null && script.isRunning()) {
            script.messageReceived(new MessageEvent(sender, message, channel, type));
            if (type == 0) {
                if (message.contains("Congratulations")) {
                    String a = message.replace("Congratulations, you just advanced a ", "").replace(" level.", "");
                    a = a.replace("Congratulations, you've just advanced a ", "");
                    a = a.replace("Congratulations, you just advanced an ", "");
                    int level = 0;
                    for (Skill skill : Skill.values()) {
                        if (a.equalsIgnoreCase(skill.name()))
                            level = Skills.getLevel(skill);
                    }

                    Inubot.getInstance().getIRCConnection().sendNotice("I am now level " + level + " in " + a);
                }
            } else if (message.contains("bot")) {
                Inubot.getInstance().getIRCConnection().sendNotice("Someone said bot!");
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
