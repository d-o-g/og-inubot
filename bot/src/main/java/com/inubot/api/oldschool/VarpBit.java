/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.Inubot;
import com.inubot.client.natives.oldschool.RSVarpBit;

public class VarpBit extends Wrapper<RSVarpBit> implements RSVarpBit {

    private final int id;

    public VarpBit(RSVarpBit raw, int id) {
        super(raw);
        this.id = id;
    }

    @Override
    public int getVarpIndex() {
        return raw.getVarpIndex();
    }

    @Override
    public int getLeft() {
        return raw.getLeft();
    }

    @Override
    public int getRight() {
        return raw.getRight();
    }

    public int getId() {
        return id;
    }

    public static VarpBit get(int id) {
        RSVarpBit raw = Inubot.getInstance().getClient().getVarpBit(id);
        return raw == null ? null : new VarpBit(raw, id);
    }

    @Override
    public String toString() {
        return String.format("(%d)<%d> ( %d -> %d | %d ) == %d", getId(), getVarpIndex(), getLeft(), getRight(), getMask(), getValue());
    }

    public boolean booleanValue() {
        return getValue() == 1;
    }
}
