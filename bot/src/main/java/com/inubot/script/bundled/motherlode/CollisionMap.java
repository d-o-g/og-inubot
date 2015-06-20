/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import com.inubot.api.oldschool.Tile;

import java.awt.*;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

//Raw collision dump, with no rocks/blockages within the map
public class CollisionMap {

    public static final int MIN_X = 3710;
    public static final int MIN_Y = 5630;

    public static final int MAX_X = 3780;
    public static final int MAX_Y = 5700;

    public static final int WIDTH  = MAX_X-MIN_X; //70 Not exact
    public static final int HEIGHT = MAX_Y-MIN_Y; //70 Not exact

    private MappedByteBuffer buffer;

    public CollisionMap(File loc) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(loc, "rw");
        buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,0,WIDTH*HEIGHT*4);
    }

    public void set(int x, int y, int value) {
        put(x,y,value);
    }

    private int getPosition(int x, int y) {
        return (y * WIDTH + x) * 4;
    }

    private void put(int x, int y, int value) {
        buffer.putInt(getPosition(x, y), value);
    }

    public void clear() {
        Util.clearMap(buffer);
        buffer = null;
    }

    public int[][] map() {
        int[][] map = new int[WIDTH][HEIGHT];
        for(int x = 0; x < WIDTH; x++) {
            for(int y = 0; y < HEIGHT; y++) {
                map[x][y] = buffer.getInt(getPosition(x,y));
            }
        }
        return map;
    }

    //Converts this raw collision into a simulated map for quicker path finding
    public static void main(String... args) throws IOException {
        simulate(new File("./flags.txt"),
                new File("./simulated.txt")
        );
    }

    public static void simulate(File src, File dest) throws IOException {
        CollisionMap map = new CollisionMap(src);
        SuccessorMap sim = new SuccessorMap(dest);
        int[][] mapped = map.map();

        for(int x = 0; x < CollisionMap.WIDTH; x++) {
            for(int y = 0; y < CollisionMap.HEIGHT; y++) {

                int flag = mapped[x][y];
                int flag0 = 0;

                for(SuccessorNode node : SuccessorNode.values()) {
                    if(node.walkable(mapped,x,y,flag)) {
                        flag0 |= (1 << node.ordinal());
                    }
                }

                //Make sure we can't pass between two diagonal rocks
                if(x > 0 && y > 0 && x < WIDTH-1 && y < HEIGHT-1) {

                    if(Mine.isBlockage(x,y+1) && Mine.isBlockage(x+1,y)) {
                        flag0 &= ~(1 << SuccessorNode.NORTH_EAST.ordinal());
                    }

                    if(Mine.isBlockage(x+1,y) && Mine.isBlockage(x,y-1)) {
                        flag0 &= ~(1 << SuccessorNode.SOUTH_EAST.ordinal());
                    }

                    if(Mine.isBlockage(x-1,y) && Mine.isBlockage(x,y-1)) {
                        flag0 &= ~(1 << SuccessorNode.SOUTH_WEST.ordinal());
                    }

                    if(Mine.isBlockage(x-1,y) && Mine.isBlockage(x,y+1)) {
                        flag0 &= ~(1 << SuccessorNode.NORTH_WEST.ordinal());
                    }

                }

                sim.put(x,y,(byte)flag0);
            }
        }
    }


    public static java.util.List<Tile> successors(Tile t, int[][] flags) {
        LinkedList<Tile> tiles = new LinkedList<Tile>();
        int x = t.getX(), y = t.getY();
        int f_x = x, f_y = y;
        int here = flags[f_x][f_y];
        for(SuccessorNode s : SuccessorNode.values()) {
            if(s.walkable(flags,f_x,f_y,here)) {
                tiles.add(new Tile(x+s.dx,y+s.dy));
            }
        }
        return tiles;
    }



    public void draw(Graphics2D g, int x, int y) {

        int[][] flags0 = map();

        for(Tile s : successors(new Tile(x,y,0),flags0)) {
            new Tile(s.getX()+MIN_X,s.getY()+MIN_Y).draw(g);
        }

    }
}