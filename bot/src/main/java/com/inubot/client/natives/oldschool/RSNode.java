package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

public interface RSNode extends ClientNative {
    RSNode getNext();
    RSNode getPrevious();
    long getUid();
}
