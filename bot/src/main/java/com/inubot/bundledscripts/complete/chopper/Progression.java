/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;

public abstract class Progression {

    protected final Tree next;

    public Progression(Tree next) {
        this.next = next;
    }

    public abstract boolean canProgress();

    public static class Basic extends Progression {

        private final int level;

        public Basic(Tree tree, int level) {
            super(tree);
            this.level = level;
        }

        @Override
        public boolean canProgress() {
            return Skills.getLevel(Skill.WOODCUTTING) >= this.level; //&& tree != currentTree?
        }
    }

    public static class Timed extends Progression {

        private final long startTime;
        private final long duration;

        public Timed(Tree next, long duration) {
            super(next);
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public boolean canProgress() {
            return System.currentTimeMillis() - startTime > duration;
        }
    }
}
