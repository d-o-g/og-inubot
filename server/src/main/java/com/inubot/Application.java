package com.inubot;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Septron
 * @since June 21, 2015
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final SessionFactory factory;

    static {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create factory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void main(String... args) {
        logger.info("Caching scripts...");
        try {
            Loader.singleton.load();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            logger.info("... Done");
            logger.info("Starting server...");
            new Server().open();
        }
    }

    public static SessionFactory factory() {
        return factory;
    }
}
