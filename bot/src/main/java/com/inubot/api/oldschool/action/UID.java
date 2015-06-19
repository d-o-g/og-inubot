/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.action;

public class UID { // 31 bit value

    // The only -interactable- entity types (Note no projectiles/graphics)
    public static final int TYPE_PLAYER = 0;
    public static final int TYPE_NPC = 1;
    public static final int TYPE_OBJECT = 2;
    public static final int TYPE_GROUND_ITEM = 3;

    public final int uid;

    public UID(int uid) {
        this.uid = uid;
    }

    public static int compile(int regionX, int regionY, int entityId, int entityType, boolean intractable) {
        regionX &= 127;    // Maximum value of 127
        regionY &= 127;    // Maximum value of 127
        entityId &= 32767;  // Maximum value of 32767
        entityType &= 3;      // Maximum value of 3
        int uid = (entityType << 29) + (entityId << 14) + (regionY << 7) + (regionX); //brackets for clarity
        if (!intractable)
            uid -= Integer.MIN_VALUE; //Set the sign bit to 1
        return uid;
    }

    public static int getRegionX(int uid) {
        return uid & 0x7f;
    }

    public static int getRegionY(int uid) {
        return uid >> 7 & 0x7f;
    }

    public static int getEntityId(int uid) {
        return uid >> 14 & 0x7fff;
    }

    public static int getEntityType(int uid) {
        return uid >> 29 & 0x3;
    }

    //Checks the sign bit, checking if it's positive or negative is a faster/clever alternative
    public static boolean isInteractable(int uid) {
        return uid > 0;
    }

    public int getRegionX() {
        return getRegionX(uid);
    }

    public int getRegionY() {
        return getRegionY(uid);
    }

    public int getEntityId() {
        return getEntityId(uid);
    }

    public int getEntityType() {
        return getEntityType(uid);
    }

    public boolean isInteractable() {
        return isInteractable(uid);
    }
}
