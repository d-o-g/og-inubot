package com.inubot.bot.irc;

import com.inubot.api.util.Random;
import com.inubot.bot.irc.commands.Say;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Septron <septron@creatres.me>
 * @since June 14, 2015
 */
public class IRCConnection implements Runnable {

    private static final String NAME_BASE = "BOT-";
    private static final String CHANNEL = "#rd-bot";
    private static final String MASTER = "Dogerina";

    private final String address;
    private final int port;

    private BufferedReader reader;
    private BufferedWriter writer;

    private Socket connection;

    private String username;

    public IRCConnection(String address, int port) {
        this.username = NAME_BASE + Random.nextInt(9999);
        this.address = address;
        this.port = port;
    }

    public boolean connect() throws IOException {
        try {
            connection = new Socket(address, port);
        } finally {
            if (connection != null) {
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
        }
        if (connection.isConnected()) {
            writer.write("NICK " + username + "\r\n");
            writer.write("USER " + username + " 8 * : Java bots are cool!\r\n");
            writer.flush();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("004")) {
                    System.out.println("Successfully logged into irc!");
                    break;
                } else if (line.indexOf("433") >= 1) {
                    System.out.println("Username already in use...");
                    username = NAME_BASE + Random.nextInt(9999);
                    return false;
                }
            }

            send("JOIN " + CHANNEL);
            return true;
        }
        return false;
    }

    private static List<IRCCommand> commands() {
        List<IRCCommand> commands = new ArrayList<>();
        commands.add(new Say());
        return commands;
    }

    private Pattern pattern() {
        return Pattern.compile("^(:(?<prefix>\\S+) )?(?<command>\\S+)( (?!:)(?<params>.+?))?( :(?<trail>.+))?$");
    }

    public void message(String message) throws IOException {
        send("PRIVMSG " + CHANNEL + " :@" + MASTER + " " + message);
    }

    public void send(String message) {
        try {
            writer.write(message + "\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (connection != null && connection.isConnected()) {
                while (connection.isConnected()) {
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                            if (line.startsWith("PING ")) {
                                send("PONG " + line.substring(5));
                            } else {
                                Matcher matcher = pattern().matcher(line);
                                if (matcher.find()) {
                                    if (matcher.group("command").equals("PRIVMSG")) {
                                        if (matcher.group("params").equals(CHANNEL)) {
                                            String message = matcher.group("trail");
                                            if (!message.contains("!"))
                                                continue;
                                            int index = message.indexOf(" ");
                                            if (index != -1) {
                                                if (!matcher.group("prefix").contains(MASTER))
                                                    continue;
                                                String[] params = message.substring(index).split(" ");
                                                String command = message.substring(1, index);
                                                for (IRCCommand c : commands()) {
                                                    c.handle(this, params);
                                                }
                                            } else {
                                                commands().stream().filter(
                                                        command -> message.substring(1).equals(command.name())).forEach(
                                                        command -> command.handle(this, null)
                                                );
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    System.out.println("Attempting to connect to IRC...");
                    if (!connect()) {
                        System.out.println("Failed to connect to IRC... Retrying...");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getUsername() {
        return this.username;
    }
} 