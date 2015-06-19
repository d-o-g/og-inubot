package org.runedream.client.natives;

public interface RSItemDefinition extends RSCacheNode {
    String getName();
    int getId();
    String[] getActions();
    String[] getGroundActions();
}
