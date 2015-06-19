package org.runedream.client;

import org.runedream.RuneDream;
import org.runedream.api.methods.Client;
import org.runedream.api.oldschool.action.tree.Action;
import org.runedream.api.util.Time;
import org.runedream.script.Script;
import org.runedream.script.Task;

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
        Script script = RuneDream.getInstance().getScriptFlux().getRunning();
        if (script != null && script.isRunning())
            script.messageReceived(type, sender, message, channel);
    }

    @ClientInvoked
    public static void onEngineTick() {
        Client.processActions();
        if (Client.GAME_TICK_SLEEP != -1)
            Time.sleep(Client.GAME_TICK_SLEEP);
        Script script = RuneDream.getInstance().getScriptFlux().getRunning();
        if (script == null || script.isPaused())
            return;
        script.getTickTasks().forEach(Task::execute);
    }
}
