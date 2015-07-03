/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.net.impl;

import com.inubot.Application;
import com.inubot.net.ServerConnection;
import com.inubot.net.Handler;

import java.io.*;
import java.sql.ResultSet;

/**
 * @author Dogerina
 * @since 03-07-2015
 */
public class LoginHandler implements Handler {

    @Override
    public short opcode() {
        return Application.LOGIN_OPCODE;
    }

    @Override
    public void handle(ServerConnection connection) throws Exception {
        try {
            DataInputStream input = new DataInputStream(connection.socket.getInputStream());
            String username = input.readUTF();
            String password = input.readUTF();

            connection.attributes.put("username", username);
            connection.attributes.put("password", password);

            boolean correct = false;

            ResultSet resultSet = connection.query("SELECT * FROM core_members WHERE name='" + username + "'");
            while (resultSet.next()) {
                String salt = "$2a$13$" + resultSet.getString(resultSet.findColumn("members_pass_salt"));
                String hash = resultSet.getString(resultSet.findColumn("members_pass_hash"));
                if (true) {//TODO...
                    correct = true;
                    connection.logger.info("Logged in as " + username);
                }
            }
            DataOutputStream output = new DataOutputStream(connection.socket.getOutputStream());
            output.writeBoolean(correct);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
