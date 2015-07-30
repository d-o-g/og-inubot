package com.inubot.bot.net.sdn;

import com.inubot.script.loader.RemoteScriptDefinition;

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
				System.out.println("opcode: " + opcode);
				switch (opcode) {
					case 0: { // REQUEST LOGIN
						dos.write(0);
						dos.writeUTF("testing");
						dos.writeUTF("penis123");
					}
					case 1: { // LOGIN RESULT
						int a = dis.read();
						System.out.println(a);
						System.out.println("Successfully authenticated.");
						dos.write(1); // REQUEST SCRIPTS
					}
					case 2: { // RECEIVE SCRIPT
						int size = dis.readInt();

						byte[] data = new byte[size];
						for (int i = 0; i < size; i++) {
							data[i] = (byte) dis.read();
						}
						System.out.println("\tReceived Script...");
						RemoteScriptDefinition.create(data);
					}
					case 3: { // SHUT DOWN
						System.out.println("Exiting...");
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
