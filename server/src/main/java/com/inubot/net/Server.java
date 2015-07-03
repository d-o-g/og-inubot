package com.inubot.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Septron
 * @since July 02, 2015
 */
public class Server implements Runnable {

    private final List<Connection> connections = new ArrayList<>();

    private final int port;

    private ServerSocket ss;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(port, 100);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            while(!ss.isClosed()) {
                try {
                    Socket socket = ss.accept();
                    if (socket != null) {
                        System.out.println("Accepted new socket from " + socket.getInetAddress());
                        Connection connection = new Connection(socket);
                        connections.add(connection);
                        new Thread(connection).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
