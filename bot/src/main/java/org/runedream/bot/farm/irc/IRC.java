package org.runedream.bot.farm.irc;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by luckruns0ut on 02/05/15.
 */
public class IRC {
    private static Socket socket = null;
    private static PrintStream out;
    private static BufferedReader in;
    private static Thread thread;

    private static String host;
    private static int port;
    private static String channel;
    private static String master;
    private static String nick;
    private static boolean initialized = false;

    public static void init(String host, int port, String nick, String master, String channel) {
        if(initialized) {
            throw new RuntimeException("Already initialized.");
        }
        IRC.host = host;
        IRC.port = port;
        IRC.nick = nick;
        IRC.master = master;
        IRC.channel = channel;

        initialized = true;
        thread = new Thread(new IRCThread());
        thread.start();
    }

    public static boolean connect() {
        if(!initialized) {
            return false;
        }
        try {
            if (socket != null) {
                socket.close();
            }
            socket = new Socket(host, port);
            out = new PrintStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // gets the name of the irc user to listen to commands from
    public static String getMaster() {
        return master;
    }

    public static String getNick() {
        return nick;
    }

    public static void changeNick(String newNick) {
        nick = newNick;
        sendLine("NICK " + newNick);
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }

    public static String getChannel() {
        return channel;
    }

    public static boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void sendLine(String line) {
        out.println(line);
    }

    // sends a private message to the master, but can be inconvenient as the bots will often appear in separate windows in the chat
    public static void sendPMToMaster(String line) {
        sendLine("PRIVMSG " + master + " :" + line);
    }

    // shows a message that looks like a public one, but only the sender and receiver can see it
    public static void sendNoticeToMaster(String line) {
        sendLine("NOTICE " + master + " :" + line);
    }

    public static void sendToChannel(String line) {
        sendLine("PRIVMSG #" + channel + " :" + line);
    }

    public static String readLine() {
        try {
            return in.readLine();
        }catch (Exception ex) {
            return null;
        }
    }
}
