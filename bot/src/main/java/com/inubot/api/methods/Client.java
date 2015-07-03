/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.oldschool.action.tree.Action;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Client {

    public static final int[] VARPBIT_MASKS = new int[32];
    private static final Queue<Node> queue = new ArrayBlockingQueue<>(2000);
    public static boolean LANDSCAPE_RENDERING_ENABLED = true;
    public static boolean MODEL_RENDERING_ENABLED = true;
    public static boolean WIDGET_RENDERING_ENABLED = true;
    public static boolean PAINTING = true;
    public static int GAME_TICK_SLEEP = -1;
    private static Node last;
    private static long lastProcess;

    /**
     * @return the size of the action queue
     */
    public static int getActionQueueSize() {
        return queue.size();
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b>
     */
    public static void processActions() {
        Iterator<Node> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Node cur = iterator.next();
            boolean fire = true;
            if (last != null && (System.currentTimeMillis() - lastProcess) < 600) {
                if (last.opcode == cur.opcode && last.arg1 == cur.arg1 && last.arg2 == cur.arg2) {
                    fire = false;
                }
            }
            if (fire) {
                cur.fire();
                lastProcess = System.currentTimeMillis();
                last = cur;
            }
            iterator.remove();
        }
    }

    static {
        int var0 = 2;
        for (int var1 = 0; var1 < 32; ++var1) {
            VARPBIT_MASKS[var1] = var0 - 1;
            var0 += var0;
        }
    }

    /**
     * @return <b>true</b> if the game client is in low memory mode, <b>false</b> otherwise
     */
    public static boolean isLowMemory() {
        return Inubot.getInstance().getClient().isLowMemory();
    }

    /**
     * Sets the games lowMemory value to the given lowMemory value
     *
     * @param lowMemory the new lowMemory value
     */
    public static void setLowMemory(boolean lowMemory) {
        Inubot.getInstance().getClient().setLowMemory(lowMemory);
    }

    /**
     * Sets the game engine tick sleep
     *
     * @param value the value to set the tick sleep to
     */
    public static void setEngineTick(int value) {
        Client.GAME_TICK_SLEEP = value;
    }

    /**
     * @param value <b>true</b> to render models, <b>false</b> otherwise
     */
    public static void setModelRendering(boolean value) {
        Client.MODEL_RENDERING_ENABLED = value;
    }

    /**
     * @param value <b>true</b> to render the sceneery, <b>false</b> otherwise
     */
    public static void setLandscapeRendering(boolean value) {
        Client.LANDSCAPE_RENDERING_ENABLED = value;
    }

    /**
     * <b>WARNING: THIS MAY AFFECT SCRIPTS THAT USE WIDGETS, AS Widget#isVisible WILL
     * ALWAYS RETURN FALSE IF THIS IS SET TO TRUE - ONLY USE THIS IF YOU DO NOT
     * REQUIRE THE USE OF WIDGETS</b>
     *
     * @param value <b>true</b> to render widgets, <b>false</b> otherwise
     */
    public static void setWidgetRendering(boolean value) {
        Client.WIDGET_RENDERING_ENABLED = value;
    }

    public static void processAction(Action action, String actionText, String targetText, int x, int y) {
        processAction(action.arg0, action.arg1, action.arg2, action.opcode, actionText, targetText, x, y);
    }

    public static void processAction(Action action, String actionText, String targetText) {
        processAction(action.arg0, action.arg1, action.arg2, action.opcode, actionText, targetText, 50, 50);
    }

    public static void processAction(int arg0, int arg1, int arg2, int opcode, String actionText, String targetText, int x, int y) {
        if (!Game.isLoggedIn())
            return;
        Node node = new Node();
        node.arg0 = arg0;
        node.arg1 = arg1;
        node.arg2 = arg2;
        node.opcode = opcode;
        node.actionText = actionText;
        node.targetText = targetText;
        queue.offer(node);
    }

    private static class Node {

        private int arg0, arg1, arg2, opcode;
        private String actionText;
        private String targetText;

        private void fire() {
            Inubot.getInstance().getClient().processAction(arg1, arg2, opcode, arg0, actionText, targetText, 50, 50);
        }
    }
}
