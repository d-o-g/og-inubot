package com.inubot.bot.net.cdn.packet;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {

    short LOGIN = 0;
    short REQUEST_SCRIPTS = 1;
    short OPENED_BOT = 2; //keep track of number of instances open
    short CLOSED_BOT = 3;

    short getOpcode();
    void encode(DataOutputStream out) throws IOException;
}
