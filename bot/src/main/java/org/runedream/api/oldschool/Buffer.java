/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool;

import org.runedream.client.natives.RSBuffer;

/**
 * @author unsigned
 * @since 06-05-2015
 */
public class Buffer extends Wrapper<RSBuffer> implements RSBuffer {

    public Buffer(RSBuffer raw) {
        super(raw);
    }

    @Override
    public byte[] getPayload() {
        return raw.getPayload();
    }

    @Override
    public int getCaret() {
        return raw.getCaret();
    }
}
