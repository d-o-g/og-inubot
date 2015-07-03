package com.inubot.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Septron
 * @since July 02, 2015
 */
public class ServerConnection implements Runnable {

    private static final String SERVER = "localhost", USERNAME = "root", PASSWORD = "dogsrcool123", DATABASE = "forum";
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConnection.class);
    public final Socket socket;
    public final Map<String, Object> attributes = new HashMap<>();
    private Connection sqlConnection;

    public ServerConnection(Socket connection) {
        this.socket = connection;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.sqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                            handler.handle(ServerConnection.this);
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

    public ResultSet query(String query) throws SQLException {
        Statement stmt = sqlConnection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }
}