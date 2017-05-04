package com.inubot.client.natives.oldschool;

public interface RSInteractableEntity extends RSGameObject {
    int getPlane();

    int getX();

    int getY();

    int getId();

    RSRenderable getRenderable();
}
