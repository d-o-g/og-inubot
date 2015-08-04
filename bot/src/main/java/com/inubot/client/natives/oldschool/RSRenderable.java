package com.inubot.client.natives.oldschool;

import com.inubot.api.oldschool.Model;
import com.inubot.client.Artificial;

public interface RSRenderable extends RSCacheNode {

    @Artificial
    Model getModel();

    @Artificial
    void setModel(Model model);
}
