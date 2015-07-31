package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;
import com.inubot.client.natives.ClientNative;

public interface RSWidget extends ClientNative {
    RSWidget[] getChildren();
    int getId();
    int getOwnerId();
    int getBoundsIndex();
    int getItemId();
    int getItemAmount();
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    int getScrollX();
    int getScrollY();
    int getType();
    int getIndex();
    int[] getItemIds();
    int[] getStackSizes();
    int getTextureId();
    String[] getActions();
    String getText();
    boolean isHidden();
    String[] getTableActions();

    @Artificial
    int getContainerX();

    @Artificial
    int getContainerY();
}
