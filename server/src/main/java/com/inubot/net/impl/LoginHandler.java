/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.net.impl;

import com.inubot.Application;
import com.inubot.net.Connection;
import com.inubot.net.Handler;

import java.io.*;

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
    public void handle(Connection connection) {
        try {
            DataInputStream input
                    = new DataInputStream(connection.socket.getInputStream());
            String username = input.readUTF();
            String password = input.readUTF();

            connection.attributes.put("username", username);
            connection.attributes.put("password", password);

            //TODO: Connect to db and check if pass is correct
            boolean correct = true;


            DataOutputStream output
                    = new DataOutputStream(connection.socket.getOutputStream());
            output.writeBoolean(correct);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
