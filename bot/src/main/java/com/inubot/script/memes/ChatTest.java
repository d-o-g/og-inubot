/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.inubot.Inubot;
import com.inubot.api.methods.Game;
import com.inubot.script.Script;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

import java.util.Stack;

/**
 * @author unsigned
 * @since 10-06-2015
 */
public class ChatTest extends Script {

    private String partner = null;
    private ChatterBotSession session = null;
    private Stack<String> stack = new Stack<>();

    @Override
    public boolean setup() {
        if (Game.isLoggedIn()) {
            try {
                this.session = new ChatterBotFactory().create(ChatterBotType.PANDORABOTS, "9d7054189e344d9d").createSession();
            } catch (Exception e) {
                System.err.println("Failed to create ChatterBot session!");
                return false;
            }
            this.partner = Game.getClient().getUsername().equals("pk3r w h i p") ? "jeromesteals" : "Dogerina";
            return this.session != null;
        }
        return false;
    }

    @Override
    public int loop() {
        if (!stack.empty()) {
            String pending = stack.pop();
            try {
                //TODO shorten replies and remove words like bot/robot?
                String reply = session.think(pending);
                System.out.println("Replying to <" + pending + "> with <" + reply + ">");
                for (char c : reply.toCharArray())
                    Inubot.getInstance().getCanvas().sendKey(c, 5);
                Inubot.getInstance().getCanvas().pressEnter();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 420;
    }

    @Override
    public void messageReceived(int type, String sender, String message, String channel) {
        if (partner == null || !sender.equals(partner) || stack.contains(message))
            return;
        System.out.println("Pushed message: " + message);
        stack.push(message);
    }
}
