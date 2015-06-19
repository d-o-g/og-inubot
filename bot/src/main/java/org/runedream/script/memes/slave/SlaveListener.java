/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.slave;

import org.runedream.script.Script;

/**
 * @author unsigned
 * @since 09-05-2015
 */
public class SlaveListener extends Script {

    @Override
    public int loop() {
        return 500;
    }

    //replace this with an IRC message listener mayb
    public void messageReceived(int type, String sender, String message, String channel) {
        sender = sender.toLowerCase();
        if (!message.startsWith("!") || (!sender.equals("dogerina") && !sender.equals("bytehound")))
            return;
        String cmd = message.substring(1, message.indexOf(' '));
        String args = message.substring(message.indexOf(' ') + 1);
        switch (cmd.toLowerCase()) {
            default: {
                break;
            }
        }
    }
}
