/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.net.cdn;

import com.inubot.bot.net.cdn.packet.Packet;
import com.inubot.bot.ui.Login;
import com.inubot.script.loader.RemoteScriptDefinition;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ServerConnection implements Runnable {

	private Socket connection;
	private DataInputStream input;
	private DataOutputStream output;
	private final String host;
	private final int port;
	private boolean running = true;

	public ServerConnection(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
	}

	public static boolean start() {
		try {
			new Thread(new ServerConnection("192.99.166.91", 1111)).start();
		} catch (IOException e) {
			System.out.println("Failed to connect... Trying again in one second...");
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(1));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			start();
		}
		return false;
	}

	private boolean authd = false;

	private void reconnect() {
		try {
				this.connection = new Socket(host, port);
				this.input = new DataInputStream(connection.getInputStream());
				this.output = new DataOutputStream(connection.getOutputStream());
			} catch (IOException ignored) {}
		try {

			output.writeInt(Packet.LOGIN);

			byte[] username = Login.getUsername().getBytes("UTF-8");
			output.writeInt(username.length);
			output.write(username);

			byte[] password = Login.getPassword().getBytes("UTF-8");
			output.writeInt(password.length);
			output.write(password);

			output.flush();
	} catch (IOException ignored) {}
	}

	@Override
	public void run() {
		while (true) {
			try {
				int value = input.read();
				switch (value) {
					case Packet.AUTH_SUCCESS: {
						System.out.println("Authenticated...");
						if (!authd)
							RemoteScriptDefinition.getNetworkedScriptDefinitions().clear();
						authd = true;
						break;
					}
					case Packet.REQUEST_SCRIPTS: {
						System.out.println("Receiving Script...");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] data = new byte[1024];
						while (input.read(data) > -1) {
							baos.write(data);
							data = new byte[1024];
						}
						byte[] D2 = baos.toByteArray();
						System.out.println("Read " + D2.length);
						RemoteScriptDefinition.addNetworkedDefinition(D2);
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
			} catch (Exception e) {
				System.out.println("Attempting to reconnect to SDN...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				reconnect();
			}
		}
	}
}

