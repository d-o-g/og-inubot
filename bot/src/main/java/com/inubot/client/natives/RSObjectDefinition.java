package com.inubot.client.natives;

import com.inubot.client.Artificial;

public interface RSObjectDefinition extends RSCacheNode {

    String getName();
    int getId();
    int getVarpIndex();
    int[] getTransformIds();
    String[] getActions();
    int getMapFunction();

    @Artificial
    RSObjectDefinition transform();
}
