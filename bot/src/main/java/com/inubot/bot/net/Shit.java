package com.inubot.bot.net;

import com.inubot.bot.net.cdn.packet.Packet;

import java.io.*;
import java.net.Socket;

/**
 * @author Septron
 * @since July 07, 2015
 */
public class Shit {

    public static void send(OutputStreamWriter out, String penis) throws IOException {
        out.write(penis, 0, penis.length());
    }

    public static void main(String... args) throws IOException {
        Socket socket = new Socket("46.101.172.127", 1111);

        OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(),
                "UTF-8");
        out.write(Packet.LOGIN);
        send(out, "testing");
        send(out, "penis123");
        out.flush();

        socket.getInputStream().read();
    }

}
