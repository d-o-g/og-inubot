/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.net.cdn;

import com.inubot.bot.net.cdn.packet.LoginPacket;
import com.inubot.bot.net.cdn.packet.Packet;
import com.inubot.bot.ui.Login;
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

	public static void main(String... args) {
		start();
	}

	private boolean authd = false;

	@Override
	public void run() {
		while (running) {
			authd = false;
			try {
				send(new LoginPacket(Login.getUsername(), Login.getPassword()));
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
								if ( !authd )
									RemoteScriptDefinition.getNetworkedScriptDefinitions().clear();
								authd = true;
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
								RemoteScriptDefinition.addNetworkedDefinition(data);
								break;
							}
							case Packet.INSTANCE_COUNT: {
								int asd = input.read();
								if (asd != 1) {
									System.exit(0);
								}
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
	}

	public void send(Packet packet) throws IOException {
		if (packet == null)
			throw new IllegalArgumentException("malformed_packet");
		output.writeByte(packet.getOpcode());
		packet.encode(output);
	}
}

