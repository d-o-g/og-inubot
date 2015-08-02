package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;

public interface RSRenderable extends RSCacheNode {

    @Artificial
    RSModel getModel();

    @Artificial
    void setModel(RSModel model);
}
