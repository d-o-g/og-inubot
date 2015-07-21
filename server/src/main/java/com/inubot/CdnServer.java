package com.inubot;

import com.inubot.model.Account;
import com.inubot.model.Owned;
import com.inubot.model.Script;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 7/18/2015.
 */
public class CdnServer {

    private static final SessionFactory factory;

    static {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create factory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void main(String... args) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("127.0.0.1", 1111));
            Loader.singleton.load();
            System.out.println("Cached " + Loader.scripts.size() + " classes!");
            new Thread(new Handler(serverSocket)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getMD5(String message)
            throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.reset();
        md5.update(message.getBytes());
        byte[] digest = md5.digest();
        return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
    }

    private static class Handler implements Runnable {

        private static final byte LOGIN = 0;
        private static final byte REQUEST_SCRIPTS = 1;
        private static final byte CLOSED_BOT = 3;
        private static final byte TEST = 4;
        private static final byte INSTANCE_COUNT = 5;

        private final Map<Integer, Integer> instances = new HashMap<>(); //userId, instanceCount
        private final ServerSocket serverSocket;

        private Handler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Accepted connection from " + socket.getInetAddress());
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    while (socket.isConnected()) {
                        if (input.available() > 0) {
                            byte opcode = input.readByte();
                            switch (opcode) {
                                case LOGIN: {
                                    StringBuilder builder = new StringBuilder();
                                    int ulength = input.readInt();
                                    for (int i = 0; i < ulength; i++) {
                                        builder.append(input.readChar());
                                    }
                                    String username = builder.toString();

                                    builder = new StringBuilder();
                                    int plength = input.readInt();
                                    for (int i = 0; i < plength; i++) {
                                        builder.append(input.readChar());
                                    }
                                    String password = builder.toString();

                                    Session session = factory.openSession();
                                    Account account = (Account) session.createQuery("from Account where username=:username")
                                            .setParameter("username", username).uniqueResult();
                                    if (account == null) {
                                        System.out.println("Nonexistent account!");
                                        return;
                                    }

                                    String hash = getMD5(getMD5(account.getSalt()) + getMD5(password));

                                    if (hash.equals(account.getPassword()) && socket.isConnected()) {
                                        if (!instances.containsKey(account.getId())) {
                                            instances.put(account.getId(), 1);
                                        } else {
                                            instances.put(account.getId(), instances.get(account.getId()) + 1);
                                        }
                                        output.writeByte(TEST);
                                        System.out.println("Successfully logged " + username + " into account! (" + account.getId() + ")");
                                        List owneds = session.createQuery("from Owned where uid=:uid").setParameter("uid", account.getId()).list();
                                        for (Object asd : owneds) {
                                            Script script = (Script) session.createQuery("from Script where id=:id")
                                                    .setParameter("id", ((Owned) asd).getId()).uniqueResult();
                                            byte[] data = Loader.scripts.get(script.getName());
                                            if (data != null) {
                                                output.writeByte(REQUEST_SCRIPTS);
                                                int nlen = script.getName().length();
                                                output.write(nlen);
                                                for (int i = 0; i < nlen; i++) {
                                                    output.writeChar(script.getName().charAt(i));
                                                }
                                                output.write(data);
                                                System.out.println("Sent: " + script.getName());
                                            }
                                        }
                                    } else {
                                        System.out.println("Failed to log " + username + " into account!");
                                    }
                                    break;
                                }
                                case CLOSED_BOT: {
                                    String name = "";
                                    int len = input.readInt();
                                    for (int i = 0; i < len; i++) {
                                        name += input.readChar();
                                    }
                                    Session session = factory.openSession();
                                    Account account = (Account) session.createQuery("from Account where username=:username")
                                            .setParameter("username", name).uniqueResult();
                                    if (account == null) {
                                        return;
                                    }
                                    if (instances.containsKey(account.getId())) {
                                        int count = instances.get(account.getId());
                                        if (count == 1) {
                                            instances.remove(account.getId());
                                        } else {
                                            instances.put(account.getId(), count - 1);
                                        }
                                    }
                                    break;
                                }
                                case INSTANCE_COUNT: {
                                    String name = "";
                                    int len = input.readInt();
                                    for (int i = 0; i < len; i++) {
                                        name += input.readChar();
                                    }
                                    Session session = factory.openSession();
                                    Account account = (Account) session.createQuery("from Account where username=:username")
                                            .setParameter("username", name).uniqueResult();
                                    if (account == null) {
                                        return;
                                    }
                                    output.writeInt(account.getUserGroup().getMaximumInstances());
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
