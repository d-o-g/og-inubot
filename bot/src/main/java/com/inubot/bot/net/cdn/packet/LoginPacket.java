/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.net.cdn.packet;

import java.io.DataOutputStream;
import java.io.IOException;

public class LoginPacket implements Packet {

    private final String username, password;

    public LoginPacket(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public byte getOpcode() {
        return Packet.LOGIN;
    }

    @Override
    public void encode(DataOutputStream out) throws IOException {
        if (username == null || password == null) {
            System.out.println("Please supply a username and password.");
            out.writeInt(1);
            return;
        }

        byte[] username = this.username.getBytes("UTF-8");
        out.writeInt(username.length);
        out.write(username);

        byte[] password = this.password.getBytes("UTF-8");
        out.writeInt(password.length);
        out.write(password);
    }
}
