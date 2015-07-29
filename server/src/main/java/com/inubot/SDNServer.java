package com.inubot;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Septron
 * @since July 29, 2015
 */
public class SDNServer implements Runnable {

	public static final SessionFactory factory;

	static {
		try {
			factory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Failed to create factory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static void main(String... args) {
		System.out.println("Caching scripts...");

		try {
			Loader.singleton.load();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SDNServer server = new SDNServer("192.99.166.91", 1111);
			new Thread(server).start();
			while (true) {
				String input = System.console().readLine();
				if (input.equalsIgnoreCase("stop")) {
					System.exit(0);
				}
			}
		}
	}

	private final String host;
	private final int port;

	public SDNServer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	private ServerSocket setup() {
		try {
			ServerSocket ss = new ServerSocket();
			ss.bind(new InetSocketAddress(host, port));
			return ss;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void run() {
		ServerSocket ss = setup();
		if (ss == null)
			return;
		while (true) {
			try {
				Socket connection = ss.accept();
				new Thread(new SDNSession(connection)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
