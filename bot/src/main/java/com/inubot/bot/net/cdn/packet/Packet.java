package com.inubot.bot.net.cdn.packet;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {

    byte LOGIN = 0;
    byte REQUEST_SCRIPTS = 1;
    byte OPENED_BOT = 2; //keep track of number of instances open
    byte CLOSED_BOT = 3;

    byte getOpcode();
    void encode(DataOutputStream out) throws IOException;
}
