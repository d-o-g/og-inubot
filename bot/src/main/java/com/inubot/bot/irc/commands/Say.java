package com.inubot.bot.irc.commands;

import com.inubot.CtrlBind;
import com.inubot.Inubot;
import com.inubot.api.methods.Players;
import com.inubot.bot.irc.IRCCommand;
import com.inubot.bot.irc.IRCConnection;

import java.io.IOException;
import java.util.Arrays;

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
        if (params.length > 0) {
            if (params[1].equals(Inubot.getInstance().getConnection().getUsername())) {
                StringBuilder builder = new StringBuilder();
                if (params[2] == null)
                    return;
                for (int i = 2; i < params.length; i++)
                    builder.append(params[i]).append(" ");
                System.out.println(builder.toString());
                for (char c : builder.toString().toCharArray())
                    Inubot.getInstance().getCanvas().sendKey(c, 20);
                Inubot.getInstance().getCanvas().pressEnter();
            }
        }
    }
}
