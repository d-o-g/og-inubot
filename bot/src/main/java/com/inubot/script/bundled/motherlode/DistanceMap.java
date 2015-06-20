/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.motherlode;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

//Maintains the distance between veins
public class DistanceMap {

    private MappedByteBuffer buffer;

    static final int N = Environment.NUM_VEINS;

    double[][] map;

    public DistanceMap(File loc) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(loc, "rw");
        int length = N * N * 8;
        buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE,0,length);
    }

    public void clear() {
        map = null;
        Util.clearMap(buffer);
        buffer = null;
    }

    int position(int from, int to) {
        return (from * N + to) * 8;
    }


    public void put(int from, int to, double d) {
        int pos = position(from, to);
        buffer.putDouble(pos,d);
    }

    public void put(Vein from, Vein to, double d) {
        put(from.getId(),to.getId(),d);
    }


    public double get(int from, int to) {
        if(map == null) map = map();
        return map[from][to];
    }

    public double get(Vein from, Vein to) {
        return get(from.getId(),to.getId());
    }


    public double[][] map() {
        double[][] map = new double[N][N];
        for(int f = 0; f < N; f++) {
            for(int t = 0; t < N; t++) {
                map[f][t] = buffer.getDouble(position(f,t));
            }
        }
        return map;
    }

    public static void update() throws IOException {

        Vein[] VEINS = Environment.VEINS;

        System.out.println("Updating " + VEINS.length + " Veins");

        SuccessorMap map = new SuccessorMap(new File("./simulated.txt"));
        DistanceMap dmap = new DistanceMap(new File("./dist.txt"));

        MineExplorer explorer = new MineExplorer(map);

        for(Vein L0 : VEINS) {
            for(Vein L1 : VEINS) {
                if(L0 == L1) continue;

                double t = explorer.getPathTime(L0.getDestination(),L1.getDestination());

                if(t == -1) {
                    throw new Error(L0.getDestination() + " -> " + L1.getDestination());
                }

                dmap.put(L0.getId(),L1.getId(),t);

                explorer.reset();

              /*  //System.out.println(t);

                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

                System.out.println(currencyFormatter.format(t));*/

            }

            System.out.println(L0.getId() + " done...");


        }

        System.out.println("... Done");

    }

    public static void main(String... args) throws IOException {
        DistanceMap map = new DistanceMap(new File("./dist.txt"));
        System.out.println(map.get(Environment.VEINS[0],Environment.VEINS[160]));
    }

}