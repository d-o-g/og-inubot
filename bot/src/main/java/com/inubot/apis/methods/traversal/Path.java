package com.inubot.apis.methods.traversal;

import com.inubot.apis.oldschool.Tile;

public interface Path extends Iterable<Tile> {

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
