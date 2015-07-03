package com.inubot;

import com.inubot.net.SQLConnection;
import com.inubot.net.Handler;
import com.inubot.net.Manager;
import com.inubot.net.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        Manager.add(new Handler() {
            @Override
            public short opcode() {
                return LOGIN_OPCODE;
            }

            @Override
            public void handle(SQLConnection connection) {
                try {
                    DataInputStream input
                            = new DataInputStream(connection.socket.getInputStream());
                    String username = input.readUTF();
                    String password = input.readUTF();

                    connection.attributes.put("username", username);
                    connection.attributes.put("password", password);

                    //TODO: Connect to db and check if pass is correct
                    boolean correct = true;


                    DataOutputStream output = new DataOutputStream(connection.socket.getOutputStream());
                    output.writeBoolean(correct);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Manager.add(new Handler() {
            @Override
            public short opcode() {
                return REQUEST_SCRIPTS;
            }

            @Override
            public void handle(SQLConnection connection) {
                //TODO: Get id of user...

                //TODO: Get the scripts assigned to the user

                //TODO: Send the scripts after encrypting the byte stream
            }
        });

        new Thread(server).start();
    }
}
