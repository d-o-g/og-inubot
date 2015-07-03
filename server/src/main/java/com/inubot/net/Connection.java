package com.inubot.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Septron
 * @since July 02, 2015
 */
public class Connection implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    public Map<String, Object> attributes = new HashMap<>();

    private final Connection instance;

    public final Socket socket;

    public Connection(Socket connection) {
        this.socket = connection;
        this.instance = this;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            new Thread("QUICK-Listener-" + System.currentTimeMillis()) {
                public void run() {
                    try {
                        Handler handler = Manager.get(input.readShort());
                        if (handler != null)
                            handler.handle(instance);
                        else
                            LOGGER.error("Unhanded opcode for " + socket.getInetAddress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
