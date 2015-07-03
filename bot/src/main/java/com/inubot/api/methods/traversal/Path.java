package com.inubot.api.methods.traversal;

import com.inubot.api.oldschool.Tile;

public interface Path extends Iterable<Tile> { //have implementations like a hardcoded path and a web path

    Tile[] toArray();
    boolean step(Option... options);

    public static enum Option {

        TOGGLE_RUN {
            @Override
            public void handle() {
                if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 20) {
                    Movement.toggleRun(true);
                }
            }
        };

        public abstract void handle();
    }
}
