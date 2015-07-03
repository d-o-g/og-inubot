package com.inubot;

import com.inubot.net.ServerConnection;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by bytehound on 7/2/2015.
 */
public class Test {

    public static void main(String... args) throws Exception {
        ServerConnection serverConnection = new ServerConnection(null);
        try {
            ResultSet resultSet = serverConnection.query("SELECT * FROM core_members WHERE name='dogerina'");
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                System.out.println("Bone #id: " + id);
                String salt = "$2a$13$" + resultSet.getString(resultSet.findColumn("members_pass_salt"));
                String hash = resultSet.getString(resultSet.findColumn("members_pass_hash"));
                System.out.println(hash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
