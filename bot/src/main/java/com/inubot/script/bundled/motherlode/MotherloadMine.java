/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.methods.*;
import com.inubot.api.methods.ItemTables.Entry;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.*;
import com.inubot.api.util.*;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.script.Script;
import com.inubot.script.Task;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * @author Brainfree
 */
public final class MotherloadMine extends Script implements Paintable {

    public static final File ROOT = new File(System.getProperty("user.home"),"mlm");
    public static final File C_MAP = new File(ROOT,"collision.txt");
    public static final File S_MAP = new File(ROOT,"simulated.txt");
    public static final File D_MAP = new File(ROOT,"dist.txt");

    public static boolean powerMode = false; //Maintain the hammer? //TODO need to add world hopping
    public static boolean expansion = false; //Use the upstairs mine?

    public static final int PAY_DIRT = 12011;
    public static final int RUNE_PICK = 1275;

    private StopWatch t = new StopWatch(0);
    private int startingLevel;
    private int startingXp;
    private int numOre;

    Mine mine;
    Machine machine;
    Banking bank;

    Path p;

    Vein cur;
    long last_anim;

    int abort = 0;


    public boolean setup() {

        try {
            SuccessorMap smap = new SuccessorMap(S_MAP);
            DistanceMap dmap = new DistanceMap(D_MAP);
            mine = new Mine(smap,dmap);
            mine.buildMap();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        machine = new Machine(this);
        bank = new Banking(this);

        loadShutdownHooks();

        startingLevel = Skills.getLevel(Skill.MINING);
        startingXp = Skills.getExperience(Skill.MINING);

        return true;

    }

    private void loadShutdownHooks() {
        addShutdownTask(new Task() {
            @Override
            public void execute() {
                System.out.println("Cleaning up...");
                mine.clear();
                mine = null;
                machine = null;
                bank = null;
                if(p != null) p.clear();
                p = null;
                cur = null;
                System.out.println("... Done");
            }
        });
    }


    public static boolean inMine() {
        return Mine.inMine(Players.getLocal().getLocation());
    }

    public static boolean hasDirt() {
        return Inventory.contains(PAY_DIRT);
    }

    public static boolean shouldBank() {
        return Inventory.getCount(new NameFilter<Entry>(true, "ore", "Coal", "Golden")) > 0;
    }

    public static boolean inLiveRange(Tile pos) {
        Player me = Players.getLocal();
        int dx = Math.abs(me.getX() - pos.getX());
        int dy = Math.abs(me.getY() - pos.getY());
        return dx < 14 && dy < 14;
    }

    public static void checkRun() {
        if(!Movement.isRunEnabled() && Movement.getRunEnergy() > 20) {
            Game.getClient().processAction(-1, 35913822, 57, 1, "Toggle Run", "", Mouse.getX(), Mouse.getY());
        }
    }

    public boolean walkTo(Tile t) {

        checkRun();

        Player me = Players.getLocal();
        if(me.getLocation().equals(t)) return true;

        if(p != null) p.clear(); //Clear the previous path...
        p = mine.getPath(me.getLocation(), t);

        if(p == null) {
            System.err.println("No path to " + t);
            return false;
        }

        final int a = abort;
        long last_move = 0;
        int lx=0,ly=0;

        while (/*isActive() && */!p.isComplete()) {

            p.step();

            if(a != abort) { //We can not reach our intended destination...
                System.err.println("ABORT");
                return false;
            }

            if(lx != me.getX() || ly != me.getY()
                    || Players.getLocal().getAnimation() != -1) { //We're mining a rock
                lx = me.getX();
                ly = me.getY();
                last_move = System.currentTimeMillis();
            }

            if((System.currentTimeMillis()-last_move) > 3500) {
                System.err.println("TIMEOUT");
                return false; //3,000 ms; Fail-safe if a blockage fell on a skipped path
            }

            try { Thread.sleep(600);
            } catch (InterruptedException e) {
                return false;
            }

        }

        return p.isComplete();

    }


    public int mine() {

        if(cur == null) {
            cur = mine.getClosest(); //Find an initial vein
        } else if(cur.isDepleted()) {
            Vein v = mine.getNext(cur);
            if(v != null) cur = v; //Maintain current vein for a reference point
            last_anim = 0;
        }

        if(cur != null) { //A vein with ore is available...
            if(Players.getLocal().getLocation().equals(cur.getDestination()) //Were standing at the correct vein
                    && Players.getLocal().getAnimation() != -1) {
                //... Were mining at the correct current vein
                last_anim = System.currentTimeMillis();
                return 250;
            } else if(System.currentTimeMillis() - last_anim > 3000) { //If we're idle for 3 seconds
                if(walkTo(cur.getDestination())) {
                    cur.mine();
                    return 600;
                }
            } else { //Were idle, but not within the timeout
                return 300;
            }
        } else {
            System.err.println("No Ore");
            return 50;
        }

        return 50;

    }

    @Override
    public int loop() {

        if(!inMine()) return 60;

        if(machine.getState() != Machine.IDLE) {
            return machine.handle();
        }

        machine.collectDroppedDirt(); //Double-Check

        if(Inventory.isFull()) {
            //Reset caches
            cur = null;
            last_anim = 0;

            if(Inventory.contains(PAY_DIRT)) {
                machine.setState(Machine.DEPOSIT);
                return 60;
            } else { //full of crap...
                if(bank.openBank()) {
                    Banking.depositAll();
                    return 600;
                }
            }

        } else {

            if (Bank.isOpen() && powerMode
                    && !Inventory.contains(Machine.HAMMER)
                    && Bank.getCount(new IdFilter<>(Machine.HAMMER)) > 0) { //Preferably fetch the hammer back

                Banking.withdraw(Machine.HAMMER);

            } else if (shouldBank()) { //Have misc junk...

                if (bank.openBank()) {
                    Banking.depositAll();
                    return 600;
                }

            } else if (!Inventory.contains(RUNE_PICK)) {

                if (bank.openBank()) {
                    Banking.withdraw(RUNE_PICK);
                    return 1000;
                }

            } else if (!powerMode && Inventory.contains(Machine.HAMMER)) {

                Inventory.dropAll(new IdFilter<>(Machine.HAMMER));
                return 600;

            } else if(Inventory.getCount(new NameFilter<ItemTables.Entry>(true,"Uncut"))>0) {

                Inventory.dropAll(new NameFilter<WidgetItem>(true,"Uncut"));
                return 600;

            } else { //OK to start mining again...

                return mine();

            }

        }

        return 600;
    }



    @Override
    public void messageReceived(int type, String sender, String message, String channel) {
        if(message == null) return;
        if(message.equals("You manage to mine some pay-dirt.")) {
            numOre++;
        } else if(message.equals("I can't reach that!")) {
            abort++;
        }
    }



    @Override
    public void render(Graphics2D g) {

        if(p != null) p.draw(g);
        if(cur != null) {
            g.setColor(Color.YELLOW);
            cur.getObjLoc().draw(g);
        }

        int bx = 10;
        int by = 15;
        int s = 15;

        int xp = Skills.getExperience(Skill.MINING) - startingXp;


        AWTUtil.drawBoldedString(g, "Mother Hound Miner", bx, by + s*0, Color.MAGENTA);
        AWTUtil.drawBoldedString(g, "Runtime: " + t.toElapsedString(), bx, by+s*1, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Mining Experience: " + formatNumber((Skills.getLevel(Skill.MINING) - startingLevel)) +
                "(<" + formatNumber(xp) + ">@" + perHour(xp) + "/h)", bx, by +s*2, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Pay-Dirt: " + numOre + "(" + perHour(numOre) + "/h)", bx, by+s*3, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Machine-State:" + machine.getStringState(), bx, by + s * 4, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Trip:" + machine.getTrip(), bx, by+s*5, Color.YELLOW);

    }

    private String perHour(int gained) {
        return formatNumber((int) ((gained) * 3600000D / (System.currentTimeMillis() - t.getStart())));
    }

    private String formatNumber(int start) {
        DecimalFormat nf = new DecimalFormat("0.0");
        return start >= 1000000 ? nf.format(((double) start / 1000000)) + "m" : start >= 1000 ? nf.format(((double) start / 1000)) + "k" : String.valueOf(start);
    }


}