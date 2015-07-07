package com.inubot;

import com.inubot.net.*;
import com.inubot.net.impl.LoginHandler;
import com.inubot.net.impl.ScriptRequestHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.ResultSet;

/**
 * @author Septron
 * @since June 21, 2015
 */
public class Application {

    public static final short LOGIN_OPCODE = 0;
    public static final short REQUEST_SCRIPTS_OPCODE = 1;
    public static final short BOT_ADDED_OPCODE = 2;
    public static final short BOT_REMOVED_OPCODE = 3;

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static SessionFactory factory;

    public static void main(String... args) {
        logger.info("Starting server...");

        try{
            factory = new Configuration().configure().buildSessionFactory();
        }catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        Server server = new Server(1111);

        Manager.add(new LoginHandler());
        Manager.add(new ScriptRequestHandler());

        new Thread(server).start();
    }

    public static SessionFactory getFactory() {
        return factory;
    }
}
