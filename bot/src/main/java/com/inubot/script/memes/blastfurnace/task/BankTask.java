package com.inubot.script.memes.blastfurnace.task;

import com.inubot.api.methods.Bank;
import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Inventory;
import com.inubot.api.util.Time;
import com.inubot.script.memes.blastfurnace.BlastFurnace;
import com.inubot.api.oldschool.GameObject;

/**
 * Created by luckruns0ut on 03/05/15.
 */
public class BankTask extends BlastFurnaceTask {
    @Override
    public boolean validate() {
        return Inventory.getCount() <= 1 || Bank.isOpen();
    }

    @Override
    public void run() {
        while(validate()) {
            System.out.println("loop");
            if (Bank.isOpen()) {
                System.out.println("Bank open");
                withdraw();
            } else {
                GameObject bank = GameObjects.getNearest("Bank chest");
                if(bank != null) {
                    bank.processAction("Use");
                }
            }
            Time.sleep(500);
        }
    }

    private void withdraw() {
        System.out.println("Withdrawing my shit...");
        Bank.depositInventory();
        Time.sleep(500);
     //   Bank.withdrawAll(w -> w.getName().contains("Coins"));
        Time.sleep(1000);
        if(BlastFurnace.getFurnaceCoal() < 27) {
        //    Bank.withdrawAll(w -> w.getName().equals("Coal"));
        } else {
           // Bank.withdrawAll(w -> w.getName().equals("Iron ore"));
        }
        Time.sleep(500);
        Bank.close();
        Time.sleep(2000);
    }
}
