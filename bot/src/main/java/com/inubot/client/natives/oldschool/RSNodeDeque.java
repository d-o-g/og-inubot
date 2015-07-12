package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

public interface RSNodeDeque extends ClientNative {
    RSNode getHead();
    RSNode getTail();
}
