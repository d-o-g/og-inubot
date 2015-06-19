/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.script.memes.rcing;

import org.runedream.api.methods.*;
import org.runedream.api.oldschool.*;
import org.runedream.api.util.filter.IdFilter;
import org.runedream.script.Script;

import java.util.EnumSet;

/**
 * @author unsigned
 * @since 01-06-2015
 */
public class Runecrafter extends Script {

    /** Air altar constants **/
    private static final Tile AIR_ALTAR         = new Tile(2986, 3293);
    private static final Tile FALLY_BANK        = new Tile(3013, 3355);

    /** Essence constants **/
    private static final int RUNE_ESSENCE       = 1436;
    private static final int PURE_ESSENCE       = 7936;
    private static final int PURE_ESSENCE_NOTED = 7937; //slaves

    private Altar altar;
    private Job job;

    private final Action[] actions = {};

    public boolean setup() {
        int lvl = Skills.getCurrentLevel(Skill.RUNECRAFTING);
        //possibly update the altar on level? so i dont have to restart when moving to fires etc
        this.altar = lvl < 9 ? Altar.AIR : lvl < 44 ? Altar.FIRE : Altar.NATURE;
        this.job = Inventory.getCount(new IdFilter<>(PURE_ESSENCE_NOTED)) > 0 ? Job.SLAVE : Job.MASTER;
        return true;
    }

    @Override
    public int loop() {
        for (Action action : actions) {
            if (action.validate()) {
                return action.getTask().handle();
            }
        }
        return 700;
    }

    private enum Job {
        /**
         * The slave will assist the master. Currently, I have only decided to do this for nature runecrafting,
         * however, doing it for fires may be quite helpful too. The slave will sell noted pure essence
         * to the general store near shilo village, and then rebuy them to get them in unnoted form.
         * The slave will then proceed to the nature altar, and trade the essence to the master. Once
         * the trade has been accepted by the master, then the slave will return to the general store.
         */
        SLAVE,
        /**
         * The masters job for nature runecrafting is to craft runes when essence is available.
         * If it is not, then it will just wait until a slave has traded. If mule mode is on, then
         * the master will 'notify' the mule to login. The mule will then login, and accept all the l00t.
         */
        MASTER,
        /**
         * The mules job is to trade and receive the masters nature runes whenever the master receives a huge
         * amount of nature runes.
         */
        MULE; //maybe just use my main, undecided atm
    }

    private enum Altar {
        AIR, FIRE, NATURE;
    }

    private interface Action {
        JobTask getTask();
        boolean validate();
    }

    private enum JobTask {
        //sell noted pure essence to general store (only for nats)
        SELL_TO_GENERAL_STORE(Job.SLAVE, EnumSet.of(Altar.NATURE)) {
            @Override
            public int handle() {
                return 600;
            }
        },
        //rebuy the sold essence to unnote
        BUY_FROM_GENERAL_STORE(Job.SLAVE, EnumSet.of(Altar.NATURE)) {
            @Override
            public int handle() {
                return 600;
            }
        },
        //walk to and enter the nature altar
        ENTER_NATURE_ALTAR(Job.SLAVE, EnumSet.of(Altar.NATURE)) {
            @Override
            public int handle() {
                return 600;
            }
        },
        //trade ess to master
        TRADE_MASTER(Job.SLAVE, EnumSet.of(Altar.NATURE)) {
            @Override
            public int handle() {
                return 600;
            }
        }, //MAYBE fire too to speed shit up, undecided atm
        //handle trade offer
        RECEIVE_TRADE(Job.MASTER, EnumSet.of(Altar.NATURE)) {
            @Override
            public int handle() {
                return 600;
            }
        },
        //walk back to general store after trading ess over
        WALK_TO_GENERAL_STORE(Job.SLAVE, EnumSet.of(Altar.NATURE)) {
            @Override
            public int handle() {
                return 600;
            }
        },
        //craft runes
        CRAFT_RUNES(Job.MASTER, EnumSet.allOf(Altar.class)) {
            @Override
            public int handle() {
                return 600;
            }
        };

        //the job role that this task is assigned to
        private final Job job;
        //the set of altars that this task is available for
        private final EnumSet<Altar> altars;

        private JobTask(Job job, EnumSet<Altar> altars) {
            this.job = job;
            this.altars = altars;
        }

        public abstract int handle();
    }
}
