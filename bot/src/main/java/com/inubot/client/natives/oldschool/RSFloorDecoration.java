package com.inubot.client.natives.oldschool;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public interface RSFloorDecoration extends RSGameObject {
    int getX();

    int getY();

    int getPlane();

    int getId();

    RSRenderable getRenderable();
}
