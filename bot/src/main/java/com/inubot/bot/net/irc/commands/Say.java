package com.inubot.bot.net.irc.commands;

import com.inubot.Bot;
import com.inubot.Inubot;
import com.inubot.bot.net.irc.IRCCommand;

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
    public void handle(String[] params) {
        if (params.length > 0) {
            if (params[1].equals(Bot.getInstance().getIRCConnection().getName())) {
                StringBuilder builder = new StringBuilder();
                if (params[2] == null)
                    return;
                for (int i = 2; i < params.length; i++)
                    builder.append(params[i]).append(" ");
                for (char c : builder.toString().toCharArray())
                    Bot.getInstance().getCanvas().sendKey(c, 20);
                Bot.getInstance().getCanvas().pressEnter();
            }
        }
    }
}
