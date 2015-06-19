package com.inubot.script.memes.blastfurnace;

import com.inubot.api.methods.Varps;
import com.inubot.script.Script;
import com.inubot.api.methods.Varps;
import com.inubot.script.Script;
import com.inubot.script.memes.blastfurnace.task.BankTask;
import com.inubot.script.memes.blastfurnace.task.BlastFurnaceTask;

/**
 * Created by luckruns0ut on 03/05/15.
 */
public class BlastFurnace extends Script {
    public static int BAR_INTERFACE_INDEX = 28;
    public static int STEEL_BAR_TAKE_INDEX = 110; // button that lets you take all steel bars, you use "Take" action


    private BlastFurnaceTask[] tasks = {
        new BankTask()
    };

    public static int getFurnaceSteelBars() {
        return (int)Math.floor(Varps.get(545)/65536);
    }

    public static int getFurnaceCoal() {
        return Varps.get(547)%65536;
    }

    @Override
    public int loop() {
        for(BlastFurnaceTask task : tasks) {
            if(task.validate()) {
                task.run();
                return 500;
            }
        }
        return 500;
    }
}
