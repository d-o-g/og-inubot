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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.ResultSet;

/**
 * @author Dogerina
 * @since 03-07-2015
 */
public class ScriptRequestHandler implements Handler {

    @Override
    public short opcode() {
        return Application.REQUEST_SCRIPTS_OPCODE;
    }

    //write opcode [header]
    //write scriptCount
    //for i = 0; i < scriptCount; i++
    //write scriptIds[i]
    //write script bytes

    @Override
    public void handle(ServerConnection connection) throws Exception {
        ResultSet member = connection.query("SELECT * FROM core_members WHERE name='" + connection.attributes.get("username") + "'");
        while (member.next()) {
            Array scripts = member.getArray("member_scripts");
            if (scripts == null)
                continue;
            int[] scriptIds = (int[]) scripts.getArray();
            connection.attributes.put("scriptIds", scriptIds);
            connection.socket.getOutputStream().write(scriptIds.length);
            for (int scriptId : scriptIds) {
                ResultSet script = connection.query("SELECT * FROM scripts WHERE script_id=" + scriptId);
                if (script.next()) {
                    connection.socket.getOutputStream().write(scriptId);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] data = Files.readAllBytes(Paths.get(new File("./scripts/" + scriptId + ".jar").toURI()));
                    baos.write(data);
                    baos.writeTo(connection.socket.getOutputStream());
                    baos.close();
                }
            }
        }
    }
}
