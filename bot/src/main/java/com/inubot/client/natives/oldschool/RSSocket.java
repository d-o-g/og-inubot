package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

import java.net.Socket;

/**
 * Created by Asus on 27/05/2017.
 */
public interface RSSocket extends ClientNative {
    Socket getBase();
}
