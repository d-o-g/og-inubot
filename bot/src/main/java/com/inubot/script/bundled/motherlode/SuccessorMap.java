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

public class SuccessorMap {

    private MappedByteBuffer buffer;
    private int[][] map;

    public SuccessorMap(File loc) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(loc, "rw");
        buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,0,CollisionMap.WIDTH*CollisionMap.HEIGHT*1);
    }

    private int position(int x, int y) {
        return y * CollisionMap.WIDTH + x;
    }
    public void put(int x, int y, byte val) {
        buffer.put(position(x,y),val);
    }

    public void clear() {
        map = null;
        Util.clearMap(buffer);
        buffer = null;
    }

    public int[][] map() {
        if(map != null) return map;
        int[][] map = new int[CollisionMap.WIDTH][CollisionMap.HEIGHT];
        for(int x = 0; x < CollisionMap.WIDTH; x++) {
            for(int y = 0; y < CollisionMap.HEIGHT; y++) {
                map[x][y] = buffer.get(position(x,y));
            }
        }
        return this.map = map;
    }

    //Successor relative the Mine Base location
    public int getSuccessors(int mx, int my) {
        if(map == null) map();
        return map[mx][my];
    }



    public java.util.List<Tile> successors(Tile t) {
        LinkedList<Tile> tiles = new LinkedList<Tile>();
        int x = t.getX(), y = t.getY();

        int flag = getSuccessors(x, y);

        if(flag == 0) return tiles;

        for(int n = 0; n < 8; n++) {
            if ((flag & (1 << n)) != 0) {
                int x0 = x + MineExplorer.DX[n];
                int y0 = y + MineExplorer.DY[n];
                tiles.add(new Tile(x0,y0));
            }
        }

        return tiles;
    }

    public void draw(Graphics2D g, int x, int y) {
        for(Tile s : successors(new Tile(x,y,0))) {
            new Tile(s.getX()+Mine.BASE_X,s.getY()+Mine.BASE_Y).draw(g);
        }

    }

}