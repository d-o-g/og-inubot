/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;

import java.util.*;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public class ProgressionType {

    //TODO make a ui to create custom progression types
    public static final ProgressionType CASH_MONEY_WEED = new ProgressionType(
            "CASHMONEYWEED 420 BLAZE IT",
            new BasicFlux(Tree.REGULAR, 1), new BasicFlux(Tree.OAK, 15), new BasicFlux(Tree.WILLOW, 30),
            new BasicFlux(Tree.MAPLE, 45), new BasicFlux(Tree.YEW, 65), new BasicFlux(Tree.MAGIC, 85)
    );

    public static final ProgressionType NO_XP_WASTE = new ProgressionType(
            "NO XP WASTE BIGDICK 99 WOODCUTTING",
            new BasicFlux(Tree.REGULAR, 1), new BasicFlux(Tree.OAK, 15), new BasicFlux(Tree.WILLOW, 30)
    );

    private final String label;
    private final List<ProgressionFlux> progressFlow;

    ProgressionType(String label,
                    ProgressionFlux... movements) {
        this.label = label;
        this.progressFlow = new ArrayList<>();
        Collections.addAll(progressFlow, movements);
    }

    public String getLabel() {
        return label;
    }

    public ProgressionFlux[] getProgressFlow() {
        return progressFlow.toArray(new ProgressionFlux[progressFlow.size()]);
    }

    public static class BasicFlux extends ProgressionFlux {

        private final int level;

        private BasicFlux(Tree tree, int level) {
            super(tree);
            this.level = level;
        }

        @Override
        public boolean canProgress() {
            return Skills.getLevel(Skill.WOODCUTTING) >= this.level; //&& tree != currentTree?
        }
    }
}
