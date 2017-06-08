package com.inubot.client.natives.oldschool;

import com.inubot.client.Artificial;
import com.inubot.client.natives.ClientNative;

public interface RSInterfaceComponent extends ClientNative {
    RSInterfaceComponent[] getChildren();
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
    int getMaterialId();
    String[] getActions();
    String getText();
    boolean isHidden();
    String[] getTableActions();

    int getConfig();

    int getXPadding();

    int getYPadding();

    int getSpriteId();

    int getModelId();

    int getShadowColor();

    int getTextColor();

    int getBorderThickness();

    int getFontId();

    String getSelectedAction();

    int getContentType();

    @Artificial
    int getContainerX();

    @Artificial
    int getContainerY();

    boolean isInteractable();
}
