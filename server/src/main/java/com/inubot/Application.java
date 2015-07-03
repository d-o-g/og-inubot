package com.inubot;

import com.inubot.net.Connection;
import com.inubot.net.Handler;
import com.inubot.net.Manager;
import com.inubot.net.Server;
import com.inubot.net.impl.LoginHandler;
import com.inubot.net.impl.ScriptRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Septron
 * @since June 21, 2015
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static final short LOGIN_OPCODE = 0;
    public static final short REQUEST_SCRIPTS = 1;

    public static void main(String... args) {
        logger.info("Starting server...");

        Server server = new Server(1111);
        Manager.add(new LoginHandler());
        Manager.add(new ScriptRequestHandler());

        new Thread(server).start();
    }
}
