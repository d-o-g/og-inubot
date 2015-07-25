package com.inubot.bundledscripts.complete.chopper;

import java.util.*;

public class ProgressionType {

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

    @Override
    public final String toString() {
        return getLabel();
    }
}
