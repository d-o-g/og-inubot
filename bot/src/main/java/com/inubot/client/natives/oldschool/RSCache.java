package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

public interface RSCache extends ClientNative {

    RSNodeTable getTable();

    RSQueue getQueue();
}
