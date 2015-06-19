package com.inubot.api.methods.traversal;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.util.Time;

/**
 * Created by luckruns0ut on 01/05/15.
 */
public class Path {
    private final Tile[] tiles;

    public Path(final Tile[] tiles) {
        this.tiles = tiles;
    }

    public int getNearestTileIndex() {
        int lowest = 69_420_1337;
        int result = 0;
        for(int i = 0; i < tiles.length; i++) {
            int dist = tiles[i].distance();
            if(dist < lowest) {
                lowest = dist;
                result = i;
            }
        }
        return result;
    }

    public void traverse() {
        int index = getNearestTileIndex();
        for(int i = index; i < tiles.length; i++) {
            Tile t = tiles[i];
            while(!t.equals(Players.getLocal().getLocation())) {
                if(!Movement.isReachable(t)) {
                    GameObject door = GameObjects.getNearest(g -> g.getName() != null && (g.getName().toLowerCase().contains("door") || g.getName().toLowerCase().contains("gate")));
                    if(door != null) {
                        door.processAction("Open");
                    }
                }
                Movement.walkTo(t);
                Time.sleep(300, 1000);
            }
        }
    }
}
