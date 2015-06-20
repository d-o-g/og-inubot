package com.inubot.bot.irc.commands;

import com.inubot.Inubot;
import com.inubot.api.methods.Players;
import com.inubot.bot.irc.IRCCommand;
import com.inubot.bot.irc.IRCConnection;

/**
 * @author Dank Memes
 * @since June 19, 2015
 */
public class Say implements IRCCommand {

    @Override
    public String name() {
        return "say";
    }

    @Override
    public void handle(IRCConnection connection, String[] params) {
        if (params.length > 2) {
            if (params[0].equals(Players.getLocal().getName())) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < (params.length - 1); i++) {
                    builder.append(params[i]);
                }
                //TODO: How the fuck do I send a message
            }
        }
    }
}
