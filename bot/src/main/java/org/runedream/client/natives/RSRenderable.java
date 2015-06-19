package org.runedream.client.natives;

import org.runedream.client.Artificial;

public interface RSRenderable extends RSCacheNode {

    @Artificial
    boolean isDrawingDisabled();

    @Artificial
    boolean isAnimationDisabled();


    @Artificial
    void setDrawingDisabled(boolean disabled);

    @Artificial
    void setAnimationDisabled(boolean disabled);
}
