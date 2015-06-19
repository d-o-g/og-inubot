package org.runedream.bot.farm.shell.commands;

import org.runedream.bot.farm.shell.Shell;

/**
 * Created by luckruns0ut on 02/05/15.
 */
public class HelpCommand extends ShellCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String process(String... args) {
        if(args.length > 0) {
            ShellCommand command = Shell.getCommand(args[0]);
            if(command != null){
                return command.getName() + ": " + command.getUsageInfo();
            } else {
                return "Unknown command: " + args[0];
            }
        }
        return null;
    }

    @Override
    public String getUsageInfo() {
        return "help [command] - If you couldn't work this out, just unplug pc.";
    }
}
