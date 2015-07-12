package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

public interface RSNodeTable extends ClientNative {

    int getIndex();

    int getSize();

    RSNode[] getBuckets();

    RSNode getHead();

    RSNode getTail();

}
