/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.oldschool.Tile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

public class MineExplorer {

    public static final double UNIT_TIME = 6 / 10D; // The time in seconds to walk one tile  10 tiles in 6 seconds when running.
    public static final double CLR_BLK_TIME = 4.0; //The empirical time cost it takes to clear/handle a blockage

    private SuccessorMap map;
    private PriorityQueue<Path> paths;
    private HashMap<Integer, Double> mindists;

    //         NW  N NE  E   SE  S   SW  W
    public static final int[] DX = new int[]{ -1, 0, 1, 1,  1,  0, -1, -1 };
    public static final int[] DY = new int[]{  1, 1, 1, 0, -1, -1, -1,  0 };


    private final class Path implements Comparable {

        public int point;
        public double f;
        public double g;
        public Path parent;
        public int depth = 0;

        public Path() {
            parent = null;
            point = 0;
            g = f = 0.0;
        }

        public Path(Path p) {
            this();
            parent = p;
            g = p.g;
            f = p.f;
            depth = p.depth+1;
        }

        public int compareTo(Object o) {
            Path p = (Path) o;
            return (int) (f - p.f);
        }

        public int getPoint() {
            return point;
        }

        public int getX() {
            return point >> 15;
        }

        public int getY() {
            return point & 0x7FFF;
        }

        public void setPoint(int p) {
            point = p;
        }

    }

    protected double g(int from, int to) {
        double time = UNIT_TIME;
        if(Mine.isBlockage(to >> 15, to & 0x7FFF)) time += CLR_BLK_TIME;
        return time;
    }

    protected double h(int from, int to) {
        final int x = (from >> 15) - (to >> 15);
        final int y = (from & 0x7FFF) - (to & 0x7FFF);
        return Math.hypot(x, y);
    }

    public MineExplorer(SuccessorMap evaluator) {
        this.map = evaluator;
        paths = new PriorityQueue<>();
        mindists = new HashMap<>();
    }


    protected double f(Path p, int from, int to) {

        double g = g(from, to) + ((p.parent != null) ? p.parent.g : 0.0);
        double h = h(from, to);

        p.g = g;
        p.f = g + h;

        return p.f;

    }

    private void expand(Path path) {

        int p = path.getPoint();

        Double min = mindists.get(p);

        if (min == null || min > path.f)
            mindists.put(path.getPoint(), path.f);
        else return;

        int x = p >> 15;
        int y = p & 0x7FFF;

        int flag = map.getSuccessors(x, y);

        if(flag == 0) return;

        for(int n = 0; n < 8; n++) {
            if ((flag & (1 << n)) != 0) {
                int pos = (x + DX[n]) << 15 | (y + DY[n]);
                Path newPath = new Path(path);
                newPath.point = pos;
                f(newPath, path.point, pos);
                paths.offer(newPath);
            }
        }

    }

    public void reset() {
        paths.clear();
        mindists.clear();
    }

    public void close() {
        reset();
        paths = null;
        mindists = null;
        map = null;
    }

    public SuccessorMap getMap() {
        return map;
    }

    public Tile[] findPath(Tile start, Tile destination) {
        assert start.getPlane() == destination.getPlane();
        start = start.derive(-Mine.BASE_X, -Mine.BASE_Y);
        destination = destination.derive(-Mine.BASE_X, -Mine.BASE_Y);
        return computePath(start.hashCode(), destination.hashCode(), start.getPlane(), Mine.BASE_X, Mine.BASE_Y);
    }

    public double getPathTime(Tile start, Tile destination) {
        assert start.getPlane() == destination.getPlane();
        start = start.derive(-Mine.BASE_X, -Mine.BASE_Y);
        destination = destination.derive(-Mine.BASE_X, -Mine.BASE_Y);
        return computePathTime(start.hashCode(), destination.hashCode(), start.getPlane(), Mine.BASE_X, Mine.BASE_Y);
    }

    public static void main(String... args) throws IOException {
        System.out.println(UNIT_TIME);
        Tile start = new Tile(3720,5670);
        Tile dest = new Tile(3732,5682);
        MineExplorer flooder = new MineExplorer(new SuccessorMap(new File("./simulated.txt")));
        System.out.println(start.hashCode());
        System.out.println(Arrays.toString(flooder.findPath(start, dest)));
    }

    public static Graphics2D g;

    private Path compute(int start, int destination, int plane, int tx, int ty) {
        try {
            Path root = new Path();
            root.setPoint(start);

            f(root, start, start);

            expand(root);

            while (true) {
                Path p = paths.poll();



                if (p == null) return null;

                //   new Tile(Mine.BASE_X + p.getX(), Mine.BASE_Y + p.getY()).draw(g);

                int last = p.getPoint();

                if (last == destination) {

                    return p;

                }

                expand(p);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private double computePathTime(int start, int destination, int plane, int tx, int ty) {
        Path p = compute(start, destination, plane, tx, ty);
        if(p == null) return -1;
        return p.g;
    }

    private int computePathLength(int start, int destination, int plane, int tx, int ty) {
        Path p = compute(start, destination, plane, tx, ty);
        if(p == null) return -1;
        return p.depth;
    }

    private Tile[] computePath(int start, int destination, int plane, int tx, int ty) {

        Path p = compute(start, destination, plane, tx, ty);
        if(p == null) return null;

        ArrayDeque<Tile> retPath = new ArrayDeque<>(p.depth);

        for (Path i = p; i != null; i = i.parent) {
            retPath.addFirst(new Tile(tx + i.getX(), ty + i.getY(), plane));
        }

        return retPath.toArray(new Tile[retPath.size()]);

    }

}