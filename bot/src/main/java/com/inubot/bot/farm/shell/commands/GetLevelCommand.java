/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.farm.shell.commands;

import com.inubot.api.methods.Skills;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;

/**
 * @author unsigned
 * @since 03-05-2015
 */
public class GetLevelCommand extends ShellCommand {

    @Override
    public String getName() {
        return "level";
    }

    @Override
    public String process(String... args) {
        if (args.length == 0)
            return "The fuck are you doing nigger? Usage: level [skill_name] example: level attack";
        for (Skill skill : Skill.values()) {
            if (args[0].equalsIgnoreCase(skill.name().toLowerCase()))
                return String.format("%s: %d", skill.name().toLowerCase(), Skills.getLevel(skill));
        }
        return "-1";
    }

    @Override
    public String getUsageInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("gets the level for the specified skill. Usage: level [skill_name] example: level attack")
                .append("\n")
                .append("Valid skills: ");
        for (Skill skill : Skill.values())
            sb.append(skill.name().toLowerCase()).append(", ");
        return sb.toString();
    }
}
