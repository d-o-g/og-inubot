package com.inubot;

import com.inubot.net.ServerConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bytehound on 7/2/2015.
 */
public class Test {

    public static void main(String... args) throws IOException {
        ServerConnection serverConnection = new ServerConnection(null);
        try {
            ResultSet resultSet = serverConnection.query("SELECT * core_members WHERE name='Bone'");
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                System.out.println("Bone #id: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
