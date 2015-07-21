package com.inubot.bot.net.cdn.packet;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {

    byte LOGIN = 0;
    byte REQUEST_SCRIPTS = 1;
    byte CLOSED_BOT = 3;
    byte AUTH_SUCCESS = 4;
    byte INSTANCE_COUNT = 5;

    byte getOpcode();
    void encode(DataOutputStream out) throws IOException;
}
