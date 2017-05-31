package me.mad;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Random;
import com.inubot.api.util.StopWatch;
import com.inubot.bot.account.Account;
import com.inubot.bot.account.AccountManager;
import com.inubot.bot.util.Configuration;
import com.inubot.script.Manifest;
import com.inubot.script.Script;
import me.mad.modules.*;
import me.mad.modules.Bank;
import me.mad.modules.Combat;
import me.mad.modules.Magic;
import me.mad.util.wrappers.ModuleWrapper;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static me.mad.Tutorial.setting;

/**
 * Created by me.mad on 7/25/15.
 */
@Manifest(name = "MadTutorial", developer = "Mad", desc = "Fast tutorial island solver.", version = 1.0)
public class MadTutorial extends Script implements Paintable {

    private final ModuleWrapper mw = new ModuleWrapper();
    private final StopWatch timer = new StopWatch(0);
    private static final LinkedList<Account> accs = new LinkedList<>();

    @Override
    public boolean setup() {
        mw.addModuleList(() -> Varps.get(281) < 1000);
        mw.addModuleList(() -> Varps.get(281) >= 1000);

        mw.submit(new Done(), 1);
        mw.submit(new RSGuide(), 0);
        mw.submit(new SurvivalExpert(), 0);
        mw.submit(new Chef(), 0);
        mw.submit(new QuestGuide(), 0);
        mw.submit(new Miner(), 0);
        mw.submit(new Combat(), 0);
        mw.submit(new Bank(), 0);
        mw.submit(new Monk(), 0);
        mw.submit(new Magic(), 0);

//        try {
//            List<String> lines = Files.readAllLines(Paths.get(Configuration.CACHE + "accs.txt"));
//            for (String line : lines) {
//                String[] split = line.split("\\|");
//                for (String acc : split) {
//                    String[] details = acc.split(":");
//                    if (details.length != 2) {
//                        continue;
//                    }
//                    accs.add(new Account(details[0], details[1]));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        nextAcc();
        return true;
    }

    public static void main(String[] args) {

        try {
            List<String> lines = Files.readAllLines(Paths.get(Configuration.CACHE + "accs.txt"));
            for (String line : lines) {
                String[] split = line.split("\\|");
                for (String acc : split) {
                    String[] details = acc.split(":");
                    if (details.length != 2) {
                        continue;
                    }
                    System.out.println(details[0] + ":" + details[1]);
                    accs.add(new Account(details[0], details[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(accs.size());
    }

    public static void nextAcc() {
        AccountManager.setCurrentAccount(accs.pop());
    }

    @Override
    public int loop() {
//        if (accs.size() == 0) {
//            stop();
//        }
        int var = setting();

        if (var > 0 && !Movement.isRunEnabled() && Movement.getRunEnergy() > 1) {
            Movement.toggleRun(true);
        }

        if (!com.inubot.api.methods.Bank.isOpen() && (var == 120 || var == 210 || var == 360 || var == 510)) {
            Inventory.dropAll(t -> true);
        }

        //state 120: drop
        //state 210: drop
        //state 360: drop
        mw.execute();
        return Random.nextInt(260, 420);
    }

    @Override
    public void render(Graphics2D g) {
        g.drawString("MadTutorial", 100, 100);
        g.drawString("Time: " + timer.toElapsedString(), 100, 115);
        if (mw.getValidModule().isPresent()) {
            g.drawString("Status: " + mw.getValidModule().get().getClass().getSimpleName(), 100, 130);
        }
        g.drawString("Setting: " + Varps.get(281), 100, 145);
    }
}
