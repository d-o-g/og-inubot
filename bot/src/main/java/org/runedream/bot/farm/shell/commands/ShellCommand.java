package org.runedream.bot.farm.shell.commands;

/**
 * Created by luckruns0ut on 02/05/15.
 */
public abstract class ShellCommand {

    public abstract String getName();

    public abstract String process(String... args);

    public abstract String getUsageInfo();

}
