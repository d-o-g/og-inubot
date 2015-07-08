package com.inubot.bot.net.sdn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Septron
 * @since July 07, 2015
 */
public class Connection implements Runnable {

    public static void main(String... args) throws IOException {
        new Thread(new Connection()).start();
    }

    private final DataOutputStream output;
    private final DataInputStream input;

    private final Socket socket;

    public Connection() throws IOException {
        socket = new Socket("127.0.0.1", 1111);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        String username = "Tester";
        String password = "penis123";
        try {
            output.writeByte(0);
            output.writeInt(username.length());
            for (int i = 0; i < username.length(); i++)
                output.writeChar(username.charAt(i));
            output.writeInt(password.length());
            for (int i = 0; i < password.length(); i++)
                output.writeChar(password.charAt(i));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
