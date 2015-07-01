package com.inubot.bot.net.irc;

/**
 * @author Septron <septron@creatres.me>
 * @since June 15, 2015
 */
public interface IRCCommand {

    String name();

    void handle(String[] params);
}