/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.client.natives;

import com.inubot.Inubot;
import com.inubot.api.methods.Client;
import com.inubot.api.methods.Varps;
import com.inubot.client.Artificial;
import com.inubot.api.oldschool.VarpBit;

public interface RSVarpBit extends ClientNative {

    int getVarpIndex();  //the index of the 'owner' varp of this bit within the Client#varps array
    int getLeft();       //lower bit
    int getRight();      //upper bit

    @Artificial //partially artificial
    default int getValue() {
        int varpValue = Varps.get(getVarpIndex());
        return getValue(varpValue);
    }

    @Artificial
    default int getBitCount() {
        return getRight() - getLeft();
    }

    @Artificial
    default int getMask() {
        return Client.VARPBIT_MASKS[getBitCount()];
    }

    @Artificial
    default int getValue(int varpValue) {
        int mask = Client.VARPBIT_MASKS[getRight() - getLeft()];
        return varpValue >> getLeft() & mask;
    }

    @Artificial
    default boolean getBoolean() {
        return isBoolean() && getValue() == 1;
    }

    @Artificial
    default boolean isBoolean() {
        return getBitCount() == 1;
    }

    @Artificial
    default int getSet(int varp, int value) {
        int mask = Client.VARPBIT_MASKS[getRight() - getLeft()];
        if (value < 0 || value > mask)
            throw new IndexOutOfBoundsException("Value out of mask range!");
        mask <<= getLeft();
        return ((varp & ~mask) | value << getLeft() & mask);
    }

    public static String toString(RSVarpBit bit) {
        return String.format("<%d> ( %d -> %d | %d ) == %d", bit.getVarpIndex(), bit.getLeft(), bit.getRight(), bit.getMask(), bit.getValue());
    }

    public static VarpBit get(int id) {
        return new VarpBit(Inubot.getInstance().getClient().getVarpBit(id), id);
    }
}
