package org.runedream.client.natives;

import org.runedream.client.Artificial;

/**
 * @author unsigned
 * @since 26-04-2015
 */
@Artificial
public interface RSGameObject extends ClientNative {
    int getX();
    int getY();
    int getPlane();
    int getId();
}
