package com.inubot;

import com.inubot.model.Account;
import com.inubot.model.Owned;
import com.inubot.model.Script;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Septron
 * @since July 29, 2015
 */
public class SDNSession implements Runnable {

	private static final Map<Integer, Integer> sessions = new HashMap<>();

	private final DataInputStream dis;
	private final DataOutputStream dos;
	private final Socket connection;

	private Account account;

	public SDNSession(Socket connection) throws IOException {
		this.connection = connection;
		this.dis = new DataInputStream(connection.getInputStream());
		this.dos = new DataOutputStream(connection.getOutputStream());
	}

	private static String getMD5(String message) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.reset();
		md5.update(message.getBytes());
		byte[] digest = md5.digest();
		return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
	}

	private void result(boolean a) throws IOException {
		dos.write(1);
		if (a) {
			dos.write(0);
		} else {
			dos.write(1);
		}
	}

	@Override
	public void run() {
		System.out.println("New session started!");
		try {
				dos.write(0);
				while (true) {
					int opcode = dis.read();
					System.out.println(opcode);
					switch (opcode) {
						case 0: {
							String username = dis.readUTF();
							String password = dis.readUTF();

							account = (Account) SDNServer.factory.openSession().createQuery("from Account where username=:username")
									.setParameter("username", username).uniqueResult();

							if (account == null) {
								System.out.println("Failed to log user " + username + " into account...");
								result(false);
								connection.close();
								return;
							} else {
								String hash = getMD5(getMD5(account.getSalt()) + getMD5(password));
								if (hash.equals(account.getPassword())) {
									System.out.println("Successfully logged user " + username + " into account.");
									result(true);
								} else {
									System.out.println("Bad password for user " + username + " password!");
									result(false);
									connection.close();
									return;
								}
							}
							break;
						}
						case 1: {
							if (account == null) {
								System.out.println("Account is null!");
								return;
							}
							List owneds = SDNServer.factory.openSession().createQuery("from Owned where uid=:uid")
									.setParameter("uid", account.getId()).list();
							for (Object asd : owneds) {
								Script script = (Script) SDNServer.factory.openSession().createQuery("from Script where id=:id")
										.setParameter("id", ((Owned) asd).getId()).uniqueResult();
								byte[] data = Loader.scripts.get(script.getName());
								if (data == null) {
									System.out.println("No data for " + script.getName());
									continue;
								}
								dos.write(2);
								dos.writeInt(data.length);
								dos.write(data);
								System.out.println("Sent: " + script.getName());
							}
							break;
						}
					}
				}
		} catch (Exception e) {
			System.out.println("Session failed...");
		}
	}
}
