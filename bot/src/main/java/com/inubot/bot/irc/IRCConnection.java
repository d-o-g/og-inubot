package com.inubot.bot.irc;

import com.inubot.api.util.Random;
import com.inubot.bot.irc.commands.Say;
import org.jibble.pircbot.PircBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Septron
 * @since June 24, 2015
 */
public class IRCConnection extends PircBot {

    private static final String NAME_BASE = "BOT-";

    private String[] MASTER = { "Septron", "Dogerina" };

    private List<IRCCommand> commands = new ArrayList<>();
    {
        commands.add(new Say());
    }

    public IRCConnection() {
        setName(NAME_BASE + Random.nextInt(1, 9999));
    }

    public final void sendNotice(String notice) {
        for (String master : MASTER) {
            sendRawLine("NOTICE " + master + " :" + notice);
        }
    }

    @Override
    public void onMessage(String channel, String sender, String login, String host, String message) {
        int index = message.indexOf(" ");
        if (index != -1) {
            String[] params = message.substring(index).split(" ");
            String command = message.substring(1, index);
            commands.stream().filter(ic -> ic.name().equals(
                    command.toLowerCase())).forEach(ic -> ic.handle(params));
        }
    }

    public void addMaster(String name) {
        String[] masters = Arrays.copyOf(MASTER, MASTER.length + 1);
        masters[masters.length] = name;
        MASTER = masters;
    }
}
