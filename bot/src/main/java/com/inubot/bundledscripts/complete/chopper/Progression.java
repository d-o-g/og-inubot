package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;

import java.util.concurrent.TimeUnit;

public abstract class Progression {

    protected final Tree next;

    public Progression(Tree next) {
        this.next = next;
    }

    public abstract boolean canProgress();

    public abstract String verbose();

    @Override
    public final String toString() {
        return verbose();
    }

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

        @Override
        public String verbose() {
            return String.format("%s at level %d", next.getName(), level);
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

        @Override
        public String verbose() {
            return String.format("%s after %d minutes", next.getName(), TimeUnit.MILLISECONDS.toMinutes(duration));
        }
    }
}
