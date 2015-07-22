/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.net.cdn;

import com.inubot.bot.net.cdn.packet.LoginPacket;
import com.inubot.bot.net.cdn.packet.Packet;
import com.inubot.script.loader.RemoteScriptDefinition;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection implements Runnable {

    private final Socket connection;
    private final DataInputStream input;
    private final DataOutputStream output;
    private final String host;
    private final int port;
    private boolean authenticated;
    private boolean running = true;

    public ServerConnection(String host, int port) throws IOException {
        this.connection = new Socket(this.host = host, this.port = port);
        this.input = new DataInputStream(connection.getInputStream());
        this.output = new DataOutputStream(connection.getOutputStream());
    }

    public static void start() {
        try {
            ServerConnection connection = new ServerConnection("127.0.0.1", 1111);
            new Thread(connection).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reauthenticate() {
        authenticated = false;
    }

    @Override
    public void run() {
        while (running) {
            reauthenticate();
            try {
                send(new LoginPacket("testing", "penis123"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection.isClosed()) {
                try {
                    connection.connect(new InetSocketAddress(host, port));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (connection.isConnected()) {
                try {
                    if (input.available() > 0) {
                        int value = input.read();
                        switch (value) {
                            case Packet.AUTH_SUCCESS: {
                                System.out.println("Authenticated...");
                                authenticated = true;
                                RemoteScriptDefinition.getNetworkedScriptDefinitions().clear();
                                break;
                            }
                            case Packet.REQUEST_SCRIPTS: {
                                System.out.println("Receiving Script...");
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                while (input.available() > 0) {
                                    byte[] data = new byte[1024];
                                    int count = input.read(data);
                                    baos.write(data);
                                }
                                byte[] data = baos.toByteArray();
                                System.out.println("read " + data.length);
                                RemoteScriptDefinition.addNetworkedDefinition(data);
                                break;
                            }
                            default: {
                                System.out.println(value);
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//        try {
//            send(new LoginPacket("testing", "penis123"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        while (true) {
//            try {
//                if (!connection.isConnected()) {
//                    connection.connect(new InetSocketAddress(host, port));
//                    continue;
//                }
//                if (!authenticated) {
//                    String username = "testing";
//                    //Inubot.getInstance().setUsername(username);
//                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                        try {
//                            output.writeByte(Packet.CLOSED_BOT);
//                            output.writeInt(username.length());
//                            for (int i = 0; i < username.length(); i++) {
//                                output.writeChar(username.charAt(i));
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }));
//                    System.out.println("ayy");
//                }
////                if (Inubot.getInstance().getMaxInstances() == -1) {
////                    output.writeByte(Packet.INSTANCE_COUNT);
////                    String username = Bot.getInstance().getUsername();
////                    output.writeInt(username.length());
////                    for (int i = 0; i < username.length(); i++) {
////                        output.writeChar(username.charAt(i));
////                    }
////                    Inubot.getInstance().setMaxInstances(input.readInt());
////                    System.out.println("Max instances: " + Bot.getInstance().getMaxInstances());
////                }
//                if (input.available() > 0) {
//                    byte opcode = input.readByte();
//                    switch (opcode) {
//                        case Packet.AUTH_SUCCESS: {
//                            System.out.println("authed");
//                            //Inubot.getInstance().setUsername("testing");
//                            authenticated = true;
//                            System.out.println(authenticated);
//                            break;
//                        }
//                        case Packet.REQUEST_SCRIPTS: {
//                            int count = input.readInt();
//                            String name = "";
//                            for (int i = 0; i < count; i++) {
//                                name += input.readChar();
//                            }
//                            byte[] buffer = new byte[2048];
//                            input.readFully(buffer);
//                            //ScriptLoaderImpl.addLive(buffer, name);
//                            break;
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void send(Packet packet) throws IOException {
        if (packet == null)
            throw new IllegalArgumentException("malformed_packet");
        output.writeByte(packet.getOpcode());
        packet.encode(output);
    }
}

