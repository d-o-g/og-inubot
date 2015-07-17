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

    private final Socket connection;
    private final DataInputStream input;
    private final DataOutputStream output;
    private volatile boolean authenticated;

    private final String host;
    private final int port;

    public ServerConnection(String host, int port) throws IOException {
        this.connection = new Socket(this.host = host, this.port = port);
        //this.connection.connect(new InetSocketAddress(this.host = host, this.port = port));
        this.input = new DataInputStream(connection.getInputStream());
        this.output = new DataOutputStream(connection.getOutputStream());
    }

    public static void main(String... args) {
        try {
            new Thread(new ServerConnection("127.0.0.1", 1111)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                }
                if (input.available() > 0) { // y is never reached ??
                    byte opcode = input.readByte();

                    System.out.println(opcode);
                    switch (opcode) {
                        case Packet.LOGIN: {
                            authenticated = true;
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
        output.writeByte(packet.getOpcode());
        packet.encode(output);
    }
}

