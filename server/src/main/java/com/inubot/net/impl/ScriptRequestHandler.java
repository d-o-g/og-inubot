/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.net.impl;

import com.inubot.Application;
import com.inubot.net.SQLConnection;
import com.inubot.net.Handler;

/**
 * @author Dogerina
 * @since 03-07-2015
 */
public class ScriptRequestHandler implements Handler {

    @Override
    public short opcode() {
        return Application.REQUEST_SCRIPTS;
    }

    @Override
    public void handle(SQLConnection connection) {
        //TODO: Get id of user...

        //TODO: Get the scripts assigned to the user

        //TODO: Send the scripts after encrypting the byte stream
    }
}
