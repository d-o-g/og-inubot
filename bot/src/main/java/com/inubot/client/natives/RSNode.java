package com.inubot.client.natives;

public interface RSNode extends ClientNative {
    RSNode getNext();
    RSNode getPrevious();
    long getUid();
}
