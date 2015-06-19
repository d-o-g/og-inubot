package org.runedream.bot.farm.shell;

import org.runedream.bot.farm.shell.commands.*;


/**
 * Created by luckruns0ut on 02/05/15.
 */
public class Shell {
    private static final ShellCommand[] COMMANDS = {
            new HelpCommand(),
            new WalkCommand(),
            new HomeTeleCommand(),
            new GetLevelCommand(),
            new InventoryCountCommand(),
            new TradeCommand(),
            new PlayerInteractCommand(),
            new NpcInteractCommand()
    };

    public static ShellCommand getCommand(String name) {
        for(ShellCommand command : COMMANDS) {
            if(command.getName().equals(name)) {
                return command;
            }
        }
        return null;
    }

    public static String run(String line) {
        System.out.println(line);
        String[] commands = line.split("&&");
        for(String command : commands) {
            command = command.trim();
            String[] args = command.split(" ");
            if(args.length == 0) {
                System.out.println("What the fuck are you doing?");
            } else {
                ShellCommand shellCommand = getCommand(args[0]);
                if(shellCommand != null) {
                    String[] trimmedArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, trimmedArgs, 0, trimmedArgs.length);
                    String out = shellCommand.process(trimmedArgs);
                    if(out != null) {
                        System.out.println(out);
                        return out;
                    }
                } else {
                    System.out.println("Unknown command: " + args[0]);
                }
            }
        }
        return null;
    }
}
