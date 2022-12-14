/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.client.natives.oldschool;

import com.inubot.client.natives.ClientNative;

public interface RSBuffer extends ClientNative {
    byte[] getPayload();
    int getCaret();
}
