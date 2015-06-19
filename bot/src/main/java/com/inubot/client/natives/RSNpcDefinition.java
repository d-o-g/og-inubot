package com.inubot.client.natives;

import com.inubot.client.Artificial;

public interface RSNpcDefinition extends RSCacheNode {

    String getName();
    String[] getActions();
    int getId();
    int getVarpIndex();
    int[] getTransformIds();

    @Artificial
    RSNpcDefinition transform();
}
