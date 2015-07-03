/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.event;

public class MessageEvent {

    private final String text, channel, source;
    private final int type;

    public MessageEvent(String source, String message, String channel, int type) {
        this.source = source;
        this.text = message;
        this.channel = channel;
        this.type = type;
    }

    public Type getType() {
        return Type.get(type);
    }

    public int getTypeId() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getChannel() {
        return channel;
    }

    public String getSource() {
        return source;
    }

    public enum Type {

        SEVERE(0),
        PLAYER(2),
        PRIVATE_MESSAGE(3),
        TRADE_RECEIVED(4),
        PRIVATE_INFO(5),
        PRIVATE_SENT(6),
        CLAN_CHAT(9),
        SERVER(11),
        TRADE_SENT(12),
        BROADCAST(14),
        EXAMINE_NPC(28),
        EXAMINE_OBJECT(29),
        ACTION(109);

        private final int typeId;

        private Type(int typeId) {
            this.typeId = typeId;
        }

        public static Type get(int type) {
            for (Type mt : Type.values()) {
                if (mt.getId() == type)
                    return mt;
            }
            return null;
        }

        public int getId() {
            return typeId;
        }
    }
}

