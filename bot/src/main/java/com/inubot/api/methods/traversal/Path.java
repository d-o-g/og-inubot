package com.inubot.api.methods.traversal;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;

/**
 * Created by luckruns0ut on 01/05/15.
 */
public class Path {

    private final Tile[] tiles;

    public Path(Tile[] tiles) {
        this.tiles = tiles;
    }

    public int getNearestTileIndex() {
        int lowest = 694201337;
        int result = 0;
        for (int i = 0; i < tiles.length; i++) {
            int dist = tiles[i].distance();
            if (dist < lowest) {
                lowest = dist;
                result = i;
            }
        }
        return result;
    }

    public void traverse() {
        int index = getNearestTileIndex();
        for (int i = index; i < tiles.length; i++) {
            Tile t = tiles[i];
            while (t.distance() > 2) {
                Movement.walkTo(t);
                Time.sleep(300, 1000);
            }
        }
    }

    public Tile[] toArray() {
        return tiles;
    }
}
