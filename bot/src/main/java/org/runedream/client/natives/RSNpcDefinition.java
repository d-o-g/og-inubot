package org.runedream.client.natives;

import org.runedream.client.Artificial;

public interface RSNpcDefinition extends RSCacheNode {

    String getName();
    String[] getActions();
    int getId();
    int getVarpIndex();
    int[] getTransformIds();

    @Artificial
    RSNpcDefinition transform();
}
