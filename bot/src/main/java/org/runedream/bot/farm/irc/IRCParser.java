package org.runedream.bot.farm.irc;

import org.runedream.bot.farm.shell.Shell;

/**
 * Created by luckruns0ut on 02/05/15.
 */
public class IRCParser {
    public static void parse(String line) {
        if(line.startsWith("PING ")) {
            IRC.sendLine("PONG " + line.substring(5));
        }
        if(line.startsWith(":" + IRC.getMaster())) {
            line = line.replaceFirst(":", "");
            int separator = line.indexOf(":");
            line = line.substring(separator + 1, line.length());
            if(line.startsWith("!")) {
                String result = Shell.run(line.replaceFirst("!", ""));
                if(result != null) {
                    IRC.sendNoticeToMaster(result);
                }
            }
        }
    }
}
