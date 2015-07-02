package com.inubot.api.methods.traversal;

import com.inubot.api.oldschool.Tile;

public interface Path { //have implementations like a hardcoded path and a web path

    Tile[] toArray();
    boolean traverse(Option... options);

    public static enum Option {
        TOGGLE_RUN
    }
}
