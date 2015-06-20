/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.UID;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.client.natives.RSInteractableEntity;
import com.inubot.client.natives.RSWidget;

/**
 * - We can tell if the sack contains any ore (0 < X <= 81).
 * - We can not directly tell how much ore the sack contains when it's not empty.
 * - We can not deposit pay-dirt into our bank.
 * - Pay-Dirt is maintained in the circuit when you log out.
 * - You can not deposit pay-dirt into the hopper when their is already pay-dirt
 *
 * A major problem is that we can not tell how full the sack is, mainly in the
 * random check cases (If we logged out, or were placed into a random state). We
 * can only tell -effectively- if the sack contains any ore (0<X<=81). As a result
 * we must ensure that when we clean we also collect the cleaned ore from the sack
 * immediately. This is so we don't run into the case that we have to drop our inventory
 * (since we can't deposit pay-dirt) in order to empty the sack so we can finally deposit
 * more pay-dirt into the hopper.
 *
 * - We must collect any existing or future cleaned ore from the sack, and ensure that the sack
 * is always empty before mining.
 *
 *
 *
 */
public class Machine {

    public static final int MACHINE_DIALOG = 193; //The error dialog

    //Object locations
    public static final Tile STRUT_N = new Tile(3742,5669);
    public static final Tile STRUT_S = new Tile(3742,5663);
    public static final Tile HAMMER_CRATE = new Tile(3752,5674);
    //Destinations
    public static final Tile SACK   = new Tile(3748,5658);
    public static final Tile HOPPER = new Tile(3748,5673);
    public static final Tile STRUT_N_DEST = new Tile(3741,5669);
    public static final Tile STRUT_S_DEST = new Tile(3741,5663);
    public static final Tile HAMMER_CRATE_DEST = new Tile(3751,5674);
    //Items
    public static final int PAY_DIRT = MotherloadMine.PAY_DIRT;
    public static final int HAMMER = 2347;

    /** Machine-Handle States **/
    public static final int IDLE     = -1;//Nothing
    public static final int DEPOSIT  = 0; //Deporting pay-dirt into the hopper
    public static final int REPAIR   = 1; //Repairing the north strut
    public static final int WITHDRAW = 2; //Withdrawing from the sack

    /** Error Ids **/
    public static final int ERR_NO_ORE = 1; //You don't have any ore
    public static final int ERR_BROKEN = 2; //The machine is broken
    public static final int ERR_BUSY   = 3; //The machine already has pay-dirt
    public static final int ERR_FULL   = 4; //The sack is full

    private int state = IDLE;
    private int trip = 0;

    private final MotherloadMine script;

    public Machine(MotherloadMine script) {
        this.script = script;
    }

    public int getState() {
        return state;
    }

    public int getTrip() {
        return trip;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStringState() {
        switch (state) {
            case IDLE: return "Idle";
            case DEPOSIT: return "Deposit";
            case REPAIR: return "Repair";
            case WITHDRAW: return "Withdraw";
        }
        return "?";
    }


    private static boolean isErrorPrompted() {
        Widget[] k = Interfaces.widgetsFor(MACHINE_DIALOG);
        return k != null && k.length > 0;
    }

    private static int parseError() {
        if(!isErrorPrompted()) return -1;
        RSWidget[] dialog = Game.getClient().getWidgets()[MACHINE_DIALOG];
        if(dialog == null) return -1;
        for(RSWidget c : dialog) {
            if(c == null) continue;
            String txt = c.getText();
            if(txt == null || txt.isEmpty()) continue;
            if(txt.contains("You don't")) return ERR_NO_ORE;
            if(txt.contains("repaired")) return ERR_BROKEN;
            if(txt.contains("You've already")) return ERR_BUSY;
            if(txt.contains("The sack")) return ERR_FULL;
            System.err.println("Unknown Error:" + txt);
        }
        return -1;
    }

    private static int awaitAndGetError() {
        //Called when the dirt was not deposited, so we should expect the dialog pretty soon.
        long t = System.currentTimeMillis();
        while (!isErrorPrompted()
                && (System.currentTimeMillis() - t) < 600) {
            sleep(100);
        }
        return parseError();
    }

    private int deposit() {
        if(script.walkTo(HOPPER)) {
            GameObject hopper = GameObjects.getNearest("Hopper");
            if (hopper != null) {
                //Do deposit, and wait for the player to finally deposit the dirt...
                hopper.processAction("Deposit");

                long t0 = System.currentTimeMillis();
                boolean has_dirt;
                //await until the dirt is deposited
                while ((has_dirt = Inventory.contains(PAY_DIRT))
                        && (System.currentTimeMillis() - t0 < 1800)) { //Keeping in mind we're next to of the hopper
                    sleep(100);
                }

                boolean deposit_ok = !has_dirt;
                if(deposit_ok) { //The dirt was deposited
                    //A possible error that might appear is that machine is broken, check.
                    if(isBroken()) {
                        state = REPAIR;
                        return 60;
                    } else {
                        // - The only error that could arise here is the machine bing broken.
                        //The ore was deposited with no error...
                        tripDone();
                    }

                } else { //The action failed, or an error occurred
                    int error = awaitAndGetError(); //If the action was good, we should expect it soon
                    System.err.println("err=" + error);
                    if(error != -1) { //An error occurred
                        //Possible errors: The machine is busy, The sack is full, or we have no ore.
                        if(error == ERR_FULL) {
                            state = WITHDRAW;
                        } else if(error == ERR_BUSY) { //The machine has not finished cleaning our previous deposit
                            //The machine could be busy because it broke while cleaning our previous deposit
                            if(isBroken()) {
                                state = REPAIR;
                            } else {
                                return 100; //The machine is currently cleaning, wait a little.
                            }
                        } else if(error == ERR_NO_ORE) {
                            state = IDLE; //Most likely synchronizing issues, abort. No trip
                        }

                    } // else; No error, and the ore was not deposited: must have been a bad action... Try again

                }

            }
        }

        return 60;

    }

    private void tripDone() {
        state = IDLE;
        trip++;
        if(trip >= 3) { //Sack should be full... Asserting 27 ore per trip
            state = WITHDRAW;
            trip = 0;
        }
    }

    public void collectDroppedDirt() {
        while (true) { //Collect until no more...
            //Assuming allot but hopefully no godly forces causes any complex cases...
            if(hasHammer() && !MotherloadMine.powerMode) { //Keep the hammer in power-mode
                Inventory.getFirst("Hammer").processAction(ActionOpcodes.ITEM_ACTION_4, "Drop");
                sleep(600);
            } else {
                GroundItem dirt = GroundItems.getNearest("Pay-dirt");
                if(dirt == null) break;
                if(script.walkTo(dirt.getLocation())) {
                    dirt.processAction("Take"); //We take that dirt!
                    sleep(600);
                }
            }
        }
    }

    private int repair() {


        if(!isBroken()) {

            collectDroppedDirt();

            if(!Inventory.contains(PAY_DIRT)) {

                //If we were repairing due to the machine being jammed with previous ore,
                //then we would have not deposited our ore into the hopper and thus the
                //inventory would have pay-dirt (so it'll never reach this statement).

                //Only way we could get here is if the repair was request after depositing

                tripDone();

                return 60;

            } else { //We were requested to repair because the machine was jammed with out previous deposit
                state = DEPOSIT;
                return 60;
            }

        } else { //The machine is broken...

            if(getHammer()) { //It's hammer time!
                if(Players.getLocal().getAnimation() != -1) return 600; //We're currently repairing?
                //Repair the machine...
                if(script.walkTo(STRUT_N_DEST)) {
                    // Repair the north strut
                    GameObject e = GameObjects.getNearest(t -> t.getLocation().equals(STRUT_N));
                    if(e == null) {
                        System.err.println("No Strut");
                        return 60;
                    }
                    e.processAction("Hammer");
                    return 600;
                }

            }

        }

        return 60;

    }

    public static boolean hasHammer() {
        return Inventory.contains(HAMMER);
    }

    private boolean getHammer() {
        if(hasHammer()) return true;
        if(Inventory.isFull()) { //Clear a inventory spot
            //asserting our inventory is not full of misc. junk
            WidgetItem w = Inventory.getFirst("Pay-dirt");
            w.processAction(ActionOpcodes.ITEM_ACTION_4, "Drop");
            long t = System.currentTimeMillis();
            while (Inventory.isFull() //Await until the spot it cleared
                    && (System.currentTimeMillis() - t) < 1200) {
                sleep(100);
            }
        } else { //Obtain a hammer
            GameObject crate = GameObjects.getNearest(t -> t.getLocation().equals(HAMMER_CRATE));
            if(crate == null) { //eek
                System.err.println("No crate");
                return false;
            } else {
                if(script.walkTo(HAMMER_CRATE_DEST)) {
                    crate.processAction("Search");
                    //await until we have a hammer...
                    long t = System.currentTimeMillis();
                    while (!hasHammer()
                            && (System.currentTimeMillis() - t) < 1200) {
                        // ^ keeping in mind were right next to the crate.
                        sleep(100);
                    }
                }
            }
        }
        return hasHammer();
    }

    private int withdraw() {

        if(Sack.isEmpty()) {
            if(!MotherloadMine.shouldBank()) { //No junk in our inventory...
                //Withdraw complete
                collectDroppedDirt(); //Collect possibly dropped item if we had to drop previous dirt to make room

                if(MotherloadMine.hasDirt()) {
                    //We must have been requested to withdraw because the machine was full
                    trip = 0;
                    state = DEPOSIT;
                    return 60;
                } else {
                    //Withdraw complete
                    state = IDLE;
                    trip = 0;
                }

            } else { //We have roaming junk...

                if(script.bank.openBank()) {
                    Banking.depositAll();
                    return 600;
                }

            }

        } else { //Sack still has ore
            //... Our task is to drain the sack completely
            if(MotherloadMine.hasDirt()) {
                //We can not bank pay-dirt, drop it for now and pick it up before we exit
                Inventory.dropAll(new IdFilter<>(PAY_DIRT));
            } else { //Inventory has no non-bankable items...
                if(MotherloadMine.shouldBank() || Inventory.isFull()) { //Ore or gold nuggets...
                    if(script.bank.openBank()) {
                        Banking.depositAll();
                        return 600;
                    }
                } else {
                    //Free of common junk, and no pay dirt: Inventory is primed for transfer.
                    //Collect the ore within the sack...
                    if (script.walkTo(SACK)) {
                        GameObject sack = GameObjects.getNearest("Empty sack");
                        if (sack == null) return 60;
                        int uid = UID.compile(sack.getRegionX(), sack.getRegionY(), sack.getId(), UID.TYPE_OBJECT, true);
                        Game.getClient().processAction(sack.getRegionX(), sack.getRegionY(), 3, uid, "", "", Mouse.getX(), Mouse.getY());
                        return 600;
                    }

                }
            }
        }

        return 60;
    }

    public int handle() {

        if(state == IDLE) return 60;

        switch (state) {
            case DEPOSIT: return deposit();
            case REPAIR: return repair();
            case WITHDRAW: return withdraw();
            default: {
                System.err.println("Unknown State:" + state);
                return 60;
            }
        }

    }

    public static boolean isBroken() {
        GameObject[] strut = GameObjects.getLoaded(new NameFilter<>("Broken strut"));
        return (strut != null && strut.length == 2);
    }

    public static void sleep(long ms) {
        try { Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

}