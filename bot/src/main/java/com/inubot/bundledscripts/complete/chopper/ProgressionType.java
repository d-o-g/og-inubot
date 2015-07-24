/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import java.util.*;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public class ProgressionType {

    //TODO make a ui to create custom progression types
    //Add progression button
    //checkboxes for level and duration
    //if level is selected, enter level and tree type to chop
    //Chops until the level is met and thereafter switches to the next tree
    //if duration is selected, enter duration in minutes and tree type to chop
    //Chops for X minutes and then moves onto the next task ect
    public static final ProgressionType BEST_CASH = new ProgressionType(
            "Cash mode",
            new Progression.Basic(Tree.REGULAR, 1),
            new Progression.Basic(Tree.OAK, 15),
            new Progression.Basic(Tree.WILLOW, 30),
            new Progression.Basic(Tree.MAPLE, 45),
            new Progression.Basic(Tree.YEW, 65),
            new Progression.Basic(Tree.MAGIC, 85)
    );

    public static final ProgressionType BEST_EXPERIENCE = new ProgressionType(
            "Experience mode",
            new Progression.Basic(Tree.REGULAR, 1),
            new Progression.Basic(Tree.OAK, 15),
            new Progression.Basic(Tree.WILLOW, 30)
    );

    public static final ProgressionType ejifojdf = new ProgressionType(
            "imdidsovsgkofkgp",
            new Progression.Timed(Tree.MAGIC, 1000 * 60 * 60), // 1 hour
            new Progression.Timed(Tree.YEW, 1000 * 60 * 60),
            new Progression.Timed(Tree.WILLOW, 1000 * 60 * 60)
    );

    private final String label;
    private final List<Progression> progressFlow;

    ProgressionType(String label, Progression... movements) {
        this.label = label;
        this.progressFlow = new ArrayList<>();
        Collections.addAll(progressFlow, movements);
    }

    public String getLabel() {
        return label;
    }

    public Progression[] getProgressFlow() {
        return progressFlow.toArray(new Progression[progressFlow.size()]);
    }
}
