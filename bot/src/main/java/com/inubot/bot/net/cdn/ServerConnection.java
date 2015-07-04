/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.net.cdn;

import com.inubot.Inubot;
import com.inubot.bot.net.cdn.packet.LoginPacket;
import com.inubot.bot.net.cdn.packet.Packet;

import java.io.*;
import java.net.*;

public class ServerConnection implements Runnable {

    private final Socket connection; //this is the connection from this client to the server. This module will be used inside the bot
    private final DataInputStream input;
    private final DataOutputStream output;
    private volatile boolean authenticated;

    private final String host;
    private final int port;

    public ServerConnection(String host, int port) throws IOException {
        this.connection = new Socket();
        this.connection.connect(new InetSocketAddress(this.host = host, this.port = port));
        this.input = new DataInputStream(connection.getInputStream());
        this.output = new DataOutputStream(connection.getOutputStream());
        new Thread(this).start();
    }

    public static void main(String... args) throws IOException {
        new Thread(new ServerConnection("46.101.172.127", 1111)).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!connection.isConnected()) {
                    connection.connect(new InetSocketAddress(host, port));
                    continue;
                }
                if (!authenticated) {
                    output.writeByte(Packet.LOGIN);
                    send(new LoginPacket("testing", "penis123"));
                    System.out.println(authenticated = input.readBoolean());
                }
                if (input.available() > 0) {
                    short opcode = input.readShort();
                    switch (opcode) {
                        case Packet.LOGIN: {
                            authenticated = input.readBoolean();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Packet packet) throws IOException {
        if (packet == null)
            throw new IllegalArgumentException("bad_packet");
        packet.encode(output);
    }
}

