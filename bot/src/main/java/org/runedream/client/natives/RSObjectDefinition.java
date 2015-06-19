package org.runedream.client.natives;

import org.runedream.client.Artificial;

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
