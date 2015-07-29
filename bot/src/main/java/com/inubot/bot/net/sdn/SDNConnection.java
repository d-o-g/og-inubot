package com.inubot.bot.net.sdn;

import com.inubot.script.loader.RemoteScriptDefinition;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Septron
 * @since July 29, 2015
 */
public class SDNConnection implements Runnable {

	public static void main(String... args) {
		new Thread(new SDNConnection("192.99.166.91", 1111)).start();
	}

	private final String host;
	private final int port;

	private DataInputStream dis;
	private DataOutputStream dos;

	public SDNConnection(String host, int port) {
		this.host = host;
		this.port = port;
	}

	private Socket connect() {
		Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(host, port));

			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			return socket;
		} catch (IOException e) {
			System.out.println("Unable to connect... Retrying in five seconds...");
		}
		return null;
	}

	@Override
	public void run() {
		Socket connection;
		while (true) {
			try {
				int opcode = dis.read();
				switch (opcode) {
					case 0: { // REQUEST LOGIN
						dos.write(0);
						dos.writeUTF("Testing");
						dos.writeUTF("penis");
						dos.flush();
					}
					case 1: { // LOGIN RESULT
						boolean authenticated = dis.readBoolean();
						if (!authenticated) {
							System.out.println("Failed to authenticate.");
							//TODO: add login popup?
						} else {
							System.out.println("Successfully authenticated.");
							dos.write(1); // REQUEST SCRIPTS
							dos.flush();
						}
					}
					case 2: { // RECEIVE SCRIPT
						System.out.println("Ayyyy");
						int size = dis.readInt();
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] data = new byte[size];
						int n;
						while ((n = dis.read(data)) != -1) {
							baos.write(data, 0, n);
							data = new byte[size];
						}
						byte[] D2 = baos.toByteArray();

						System.out.println(D2.length);
						System.out.println(size);

						if (D2.length != size) {
							System.out.println("Fuck");
						} else {
							System.out.println("\tReceived Script...");
							RemoteScriptDefinition.create(data);
						}
					}
					case 3: { // SHUT DOWN
						System.exit(0);
					}
				}
			} catch (Exception e) {
				connection = connect();
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
