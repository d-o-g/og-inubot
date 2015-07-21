/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.net.cdn;

import com.inubot.Bot;
import com.inubot.Inubot;
import com.inubot.bot.net.cdn.packet.LoginPacket;
import com.inubot.bot.net.cdn.packet.Packet;
import com.inubot.script.loader.ScriptLoaderImpl;

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
        this.input = new DataInputStream(connection.getInputStream());
        this.output = new DataOutputStream(connection.getOutputStream());
    }

    public synchronized static ServerConnection start() {
        try {
            ServerConnection con = new ServerConnection("127.0.0.1", 1111);
            new Thread(con).start();
            return con;
        } catch (IOException e) {
            return null;
        }
    }

    public void reauthenticate() {
        authenticated = false;
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
                    String username = "testing";
                    Inubot.getInstance().setUsername(username);
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            output.writeByte(Packet.CLOSED_BOT);
                            output.writeInt(username.length());
                            for (int i = 0; i < username.length(); i++) {
                                output.writeChar(username.charAt(i));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }));
                    send(new LoginPacket(username, "penis123"));
                }
                if (Inubot.getInstance().getMaxInstances() == -1) {
                    output.writeByte(Packet.INSTANCE_COUNT);
                    String username = Bot.getInstance().getUsername();
                    output.writeInt(username.length());
                    for (int i = 0; i < username.length(); i++) {
                        output.writeChar(username.charAt(i));
                    }
                    Inubot.getInstance().setMaxInstances(input.readInt());
                    System.out.println("Max instances: " + Bot.getInstance().getMaxInstances());
                }
                if (input.available() > 0) {
                    byte opcode = input.readByte();
                    switch (opcode) {
                        case Packet.AUTH_SUCCESS: {
                            System.out.println("authed");
                            Inubot.getInstance().setUsername("testing");
                            authenticated = true;
                            break;
                        }
                        case Packet.REQUEST_SCRIPTS: {
                            int count = input.readInt();
                            String name = "";
                            for (int i = 0; i < count; i++) {
                                name += input.readChar();
                            }
                            byte[] buffer = new byte[2048];
                            input.readFully(buffer);
                            ScriptLoaderImpl.addLive(buffer, name);
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
            throw new IllegalArgumentException("malformed_packet");
        output.writeByte(packet.getOpcode());
        packet.encode(output);
    }
}

