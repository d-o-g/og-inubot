package com.inubot;

import com.inubot.net.ScriptDeliveryServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Septron
 * @since June 21, 2015
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String... args) {
        logger.info("Starting server...");
        new Thread(new ScriptDeliveryServer("127.0.0.1", 40506)).start();
    }
}
