/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.event;

public final class MessageEvent {

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

        /**
         * A severe game message
         */
        SEVERE(0),
        /**
         * A message sent by a player
         */
        PLAYER(2),
        /**
         * A private message
         */
        PRIVATE_MESSAGE(3),
        /**
         * A trade received message
         */
        TRADE_RECEIVED(4),
        /**
         * ??
         */
        PRIVATE_INFO(5),
        /**
         * A private message sent by you
         */
        PRIVATE_SENT(6),
        /**
         * A clanchat message
         */
        CLAN_CHAT(9),
        /**
         * A server message
         */
        SERVER(11),
        /**
         * A message which is a trade request sent by you
         */
        TRADE_SENT(12),
        /**
         * A message which is returned from examining an Npc
         */
        EXAMINE_NPC(28),
        /**
         * A message which is returned from examining an Object
         */
        EXAMINE_OBJECT(29),
        /**
         * A message returned from any other action
         */
        ACTION(109);

        private final int typeId;

        private Type(final int typeId) {
            this.typeId = typeId;
        }

        public static Type get(final int type) {
            for (final Type mt : Type.values()) {
                if (mt.getId() == type) return mt;
            }
            return null;
        }

        public int getId() {
            return typeId;
        }
    }
}

