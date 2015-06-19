package com.inubot.client.natives;

import com.inubot.client.Artificial;
import com.inubot.client.Artificial;

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
