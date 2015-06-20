/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Tile;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Path {

    private Segment[] segs;

    private int idx = 0;

    private Path(Segment[] segs) {
        this.segs = segs;
    }

    public void step() {

        if(isComplete()) return;

     /*   System.out.println("Step:");
        System.out.println(segs[idx]);*/

        if(segs[idx].handle()) {
            //   System.out.println("Handled");
            idx++;
        }

    }

    public boolean isComplete() {
        return idx >= segs.length;
    }

    public static void main(String... args) {
        Tile[] path = new Tile[] {
                new Tile(Mine.BASE_X,Mine.BASE_Y), new Tile(Mine.BASE_X+1,Mine.BASE_Y+1), Mine.BLOCKAGES[0].getLocation(),
                new Tile(Mine.BASE_X+2,Mine.BASE_Y+3), new Tile(Mine.BASE_X+50,Mine.BASE_Y+50), new Tile(Mine.BASE_X+6,Mine.BASE_Y+6), Mine.BLOCKAGES[1].getLocation(), Mine.BLOCKAGES[2].getLocation()
        };

        System.out.println(create(path));
    }

    public static Path create(Tile[] path) {

        ArrayDeque<Segment> segments = new ArrayDeque<>();

        int path_len = 0;
        int last_seg = 0;

        for(int i = 0; i < path.length; i++) {
            Blockage b = Mine.getBlockage(path[i]);
            if(b != null) {
                Tile[] path0 = new Tile[path_len];
                System.arraycopy(path,last_seg,path0,0,path_len);
                segments.add(new Segment(path0,b));
                last_seg = i+1;
                path_len = 0;
            } else {
                path_len++;
            }
        }

        if(path_len>0) {
            Tile[] path0 = new Tile[path_len];
            System.arraycopy(path,last_seg,path0,0,path_len);
            segments.add(new Segment(path0,null));
        }

        Segment[] segs = segments.toArray(new Segment[segments.size()]);

        return new Path(segs);

    }

    static class Segment {

        Tile[] path;
        Blockage blockage;
        Tile end;

        public Segment(Tile[] path, Blockage blockage) {
            this.path = path;
            this.blockage = blockage;
            this.end = path.length == 0
                    ? blockage.getLocation()
                    : path[path.length - 1];
        }

        /**
         * If the block is available to evaluate, check if we need to clear it.
         * Until we can verify the blockage, walk the path leading up to the rock.
         */
        public boolean handle() {
            if(blockage != null) {
                if(blockage.isUnblocked()) {
                    return true;
                } else if(blockage.inRange()) { //Blocked and within range
                    return blockage.unblock();
                } else { //Walk up to the rock until were in range
                    walkPath();
                    return false;
                }
            } else { //Standard traversal
                if(Players.getLocal().distance(end) <= 1) {
                    return true;
                } else {
                    walkPath();
                    return false;
                }
            }
        }

        private void walkPath() {

            Tile me = Players.getLocal().getLocation();
            int bx = me.getX(), by = me.getY();
            Tile next = null;
            //Find the farthest tile down the path that's within the region (so we can walk to it).
            for(Tile t : path) {
                int dx = Math.abs(t.getX() - bx);
                int dy = Math.abs(t.getY() - by);
                if(dx < 14 && dy < 14) {
                    next = t;
                }
            }

            System.out.println("Walk: " + next);

            if(next != null) {
                Movement.walkTo(next);
                //TODO wait to start/finish walking
            }

        }

        public void clear() {
            path = null;
            blockage = null;
        }

        public String toString() {
            return "(" + blockage + "):" + Arrays.toString(path);
        }

        public void draw(Graphics2D g) {

            g.setColor(Color.GREEN);
            for(Tile t : path) {
                t.draw(g);
            }

            if(blockage != null) {
                g.setColor(Color.RED);
                blockage.getLocation().draw(g);
            }

        }

    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Path: ").append(segs.length).append("\n");
        for(int i = 0; i < segs.length; i++) {
            builder.append(i).append(":").append(segs[i]).append("\n");
        }
        return builder.toString();
    }

    public void clear() {
        for(Segment s : segs) {
            s.clear();
        }
        segs = null;
    }

    public void draw(Graphics2D g) {
        if(!isComplete()) {
            segs[idx].draw(g);
        }
    }

}