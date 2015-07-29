package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.oldschool.Area;

public enum Location {

    DRAYNOR_TREES(new Area(3074, 3262, 3086, 3277), Tree.REGULAR),
    DRAYNOR_OAK(new Area(3101, 3241, 3102, 3246), Tree.OAK),
    DRAYNOR_WILLOWS(new Area(3080, 3226, 3093, 3240), Tree.WILLOW),
    FALADOR_YEWS(new Area(2992, 3309, 3045, 3323), Tree.YEW),
    GE_TREES(new Area(3143, 3448, 3174, 3467), Tree.REGULAR),
    VARROCK_WEST_TREES(new Area(3158, 3399, 3172, 3421), Tree.REGULAR),
    VARROCK_WEST_OAKS(new Area(3161, 3407, 3172, 3425), Tree.OAK),
    VARROCK_EAST_TREES(new Area(3271, 3427, 3286, 3459), Tree.REGULAR),
    VARROCK_EAST_OAKS(new Area(3271, 3411, 3286, 3441), Tree.OAK),
    VARROCK_EAST_YEWS(new Area(3268, 3463, 3308, 3479), Tree.YEW),
    VARROCK_CASTLE_YEWS(new Area(3203, 3498, 3226, 3506), Tree.YEW),
    CASTLE_WARS_TREES(new Area(2452, 3081, 2474, 3119), Tree.REGULAR),
    CASTLE_WARS_OAKS(new Area(2452, 3081, 2474, 3119), Tree.OAK),
    CATHERBY_WILLOWS(new Area(2779, 3424, 2789, 3432), Tree.WILLOW),
    CATHERBY_YEWS(new Area(2752, 3424, 2771, 3437), Tree.YEW),
    EDGEVILLE_YEWS(new Area(3085, 3468, 3090, 3484), Tree.YEW),
    SEERS_WILLOWS(new Area(2707, 3508, 2716, 3518), Tree.WILLOW),
    SEERS_MAPLES(new Area(2719, 3499, 2734, 3505), Tree.MAPLE),
    SEERS_YEWS(new Area(2704, 3458, 2718, 3468), Tree.YEW),
    SEERS_MAGICS_NORTH(new Area(2689, 3421, 2699, 3432), Tree.MAGIC),
    SEERS_MAGICS_SOUTH(new Area(2698, 3395, 2707, 3402), Tree.MAGIC),
    RIMMINGTON_YEWS(new Area(2931, 3222, 2945, 3237), Tree.YEW);

    private final Area treeArea;
    private final int safeCombatLevel;
    private final Tree[] availableTrees;

    private Location(Area treeArea, int safeCombatLevel, Tree... availableTrees) {
        this.treeArea = treeArea;
        this.safeCombatLevel = safeCombatLevel;
        this.availableTrees = availableTrees;
    }

    private Location(Area treeArea, Tree... availableTrees) {
        this(treeArea, 0, availableTrees);
    }

    public Area getTreeArea() {
        return treeArea;
    }

    public int getMinimumCombatLevel() {
        return safeCombatLevel;
    }

    public Tree[] getAvailableTrees() {
        return availableTrees;
    }

    public static Location getClosestWithTree(Tree tree) {
        Location closest = null;
        int distance = Integer.MAX_VALUE;
        for (Location location : values()) {
            boolean containsTree = false;
            for (Tree tree0 : location.getAvailableTrees()) {
                if (tree0 == tree) {
                    containsTree = true;
                }
            }
            if (!containsTree) {
                continue;
            }
            if (closest == null) {
                closest = location;
                distance = location.getTreeArea().getStart().distance();
                continue;
            }
            int dist = location.getTreeArea().getStart().distance();
            if (dist < distance) {
                distance = dist;
                closest = location;
            }
        }
        return closest;
    }
}
