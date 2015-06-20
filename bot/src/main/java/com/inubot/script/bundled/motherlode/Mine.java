/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Player;
import com.inubot.api.oldschool.Tile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

public class Mine {

    public static final int BASE_X = 3710;
    public static final int BASE_Y = 5630;

    public static final int MAX_X = 3780;
    public static final int MAX_Y = 5700;

    public static final int WIDTH  = MAX_X- BASE_X; //70 Not exact
    public static final int HEIGHT = MAX_Y- BASE_Y; //70 Not exact


    public static Blockage[] BLOCKAGES = Environment.BLOCKAGES;
    public static Vein[] VEINS = Environment.VEINS;

    public static final Blockage[][] BLOCKAGE_MAP;


    static {
        BLOCKAGE_MAP = new Blockage[WIDTH][HEIGHT];
        for(Blockage b : BLOCKAGES) {
            Tile loc = b.getLocation();
            int mx = loc.getX() - BASE_X;
            int my = loc.getY() - BASE_Y;
            BLOCKAGE_MAP[mx][my] = b;
        }
    }


    private final MineExplorer explorer;
    private final DistanceMap dmap;

    //Sorted map for telling the nearest in-order veins from any vein.
    private Vein[][] SEARCH_MAP ;

    public Mine(SuccessorMap map, DistanceMap dmap) {
        this.explorer = new MineExplorer(map);
        this.dmap = dmap;
    }

    void clear() {
        SEARCH_MAP = null;
        explorer.close();
        dmap.clear();
    }

    private final class Comp implements Comparator<Vein> {
        final Vein root;
        Comp(Vein root) {
            this.root = root;
        }
        @Override
        public int compare(Vein o1, Vein o2) {
            double d0 = dmap.get(root,o1);
            double d1 = dmap.get(root,o2);
            return Double.compare(d0,d1);
        }
    }

    void buildMap() {
        final int N = Environment.NUM_VEINS;
        SEARCH_MAP = new Vein[N][N-1];
        for(int V0 = 0; V0 < N; V0++) {
            int i = 0;
            for(int V1 = 0; V1 < N; V1++) {
                if(V0 == V1) continue;
                SEARCH_MAP[V0][i++] = VEINS[V1];
            }
            Arrays.sort(SEARCH_MAP[V0], new Comp(VEINS[V0]));
        }
    }

    public Tile[] getTilePath(Tile from, Tile to) {
        Tile[] path = explorer.findPath(from,to);
        explorer.reset();
        return path;
    }

    public Path getPath(Tile from, Tile to) {
        Tile[] path = getTilePath(from, to);
        if(path == null) return null;
        return Path.create(path);
    }

    public static boolean inMine(Tile t) {
        int dx = t.getX() - BASE_X;
        int dy = t.getY() - BASE_Y;
        return dx > 0 && dy < WIDTH
                && dy > 0 && dy < HEIGHT;
    }

    public static boolean isBlockage(Tile t) {
        assert inMine(t);
        return isBlockage(t.getX() - BASE_X, t.getY() - BASE_Y);
    }

    public static Blockage getBlockage(Tile t) {
        return getBlockage(t.getX() - BASE_X, t.getY() - BASE_Y);
    }

    public static Blockage getBlockage(int mx, int my) {
        return BLOCKAGE_MAP[mx][my];
    }

    public static boolean isBlockage(int mx, int my) {
        return BLOCKAGE_MAP[mx][my] != null;
    }

    public double getDistance(Vein from, Vein to) {
        return dmap.get(from,to);
    }

    public Vein getNext(Vein from) {
        for(Vein next : SEARCH_MAP[from.getId()]) {
            if(next.hasOre()) return next;
        }
        return null;
    }

    public Vein getClosest() {

        Player me = Players.getLocal();

        double dist = Double.MAX_VALUE;
        Vein best = null;

        for(Vein v : VEINS) {
            double d = Math.hypot(
                    me.getX()-v.getDestination().getX(),
                    me.getY()-v.getDestination().getY());
            if(d < dist) {
                dist = d;
                best = v;
            }
        }

        return best;

    }

    public static void main(String[] args) throws IOException {
       /* Mine mine = new Mine(new SuccessorMap(new File("C:\\Users\\Jamie\\Desktop\\mlm\\simulated.txt")));
        System.out.println(mine.getPath(new Tile(3720,5670), new Tile(3732,5682)));*/
    }
}