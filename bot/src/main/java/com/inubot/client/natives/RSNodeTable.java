package com.inubot.client.natives;

public interface RSNodeTable extends ClientNative {

    int getIndex();

    int getSize();

    RSNode[] getBuckets();

    RSNode getHead();

    RSNode getTail();

}
