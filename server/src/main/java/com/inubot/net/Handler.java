package com.inubot.net;

/**
 * @author Septron
 * @since July 02, 2015
 */
public interface Handler {

    short opcode();

    void handle(Connection connection);
}
