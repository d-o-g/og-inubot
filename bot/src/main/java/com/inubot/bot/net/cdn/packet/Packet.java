package com.inubot.bot.net.cdn.packet;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {

    short LOGIN = 0;
    short REQUEST_FREE_SCRIPT = 1;
    short REQUEST_VIP_SCRIPT = 2;
    short REQUEST_SCREENSHOT = 3;
    short KILL_BOT = 4;
    short OPENED_BOT = 5;
    short CLOSED_BOT = 6;

    short getOpcode();
    void encode(DataOutputStream out) throws IOException;
}
