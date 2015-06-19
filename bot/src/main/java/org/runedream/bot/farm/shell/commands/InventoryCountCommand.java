package org.runedream.bot.farm.shell.commands;

import org.runedream.api.methods.Inventory;
import org.runedream.api.methods.Players;
import org.runedream.api.oldschool.WidgetItem;

/**
 * Created by luckruns0ut on 12/05/15.
 */
public class InventoryCountCommand extends ShellCommand {
    @Override
    public String getName() {
        return "inventorycount";
    }

    @Override
    public String process(String... args) {
        if(args.length == 0)
            return "no args m8";
        WidgetItem it = Inventory.getFirst(w -> args[0].equals("" + w.getId()) || args[0].equals(w.getName()));
        return "Inventory [" + args[0] +"] for player [" + Players.getLocal().getName() + "] = " + (it != null ? it.getQuantity() : 0);
    }

    @Override
    public String getUsageInfo() {
        return getName() + " id - example: \"" + getName() + " 995\" would return the amount of coins in the inventory.";
    }
}
