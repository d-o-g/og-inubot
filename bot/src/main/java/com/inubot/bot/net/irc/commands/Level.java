package com.inubot.bot.net.irc.commands;

import com.inubot.Bot;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;
import com.inubot.bot.net.irc.IRCCommand;

/**
 * @author Dank Memes
 * @since June 19, 2015
 */
public class Level implements IRCCommand {
    @Override
    public String name() {
        return "level";
    }

    @Override
    public void handle(String[] params) {
        if (params.length >= 1) {
            String lvl = params[1];
            for (Skill skill : Skill.values()) {
                if (lvl.equals(skill.toString())) {
                    Bot.getInstance().getIRCConnection().sendNotice("Level " + Skills.getLevel(skill) + " " + skill.toString());
                }
            }
        }
    }
}
