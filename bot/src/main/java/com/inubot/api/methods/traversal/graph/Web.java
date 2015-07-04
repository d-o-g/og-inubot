package com.inubot.api.methods.traversal.graph;

import com.inubot.Inubot;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.graph.data.*;
import com.inubot.api.oldschool.Locatable;
import com.inubot.api.util.Digraph;
import com.inubot.api.util.filter.Filter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

public class Web extends Digraph<WebVertex, WebVertex> {

    private static final DijkstraPathfinder PATHFINDER = new DijkstraPathfinder();

    public Web() {
        try {
            InputStream in = new FileInputStream("./web.txt");
            Scanner s = new Scanner(in);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] kek = line.split(" ");
                int index = Integer.parseInt(kek[0]);
                String type = kek[1];
                int x = Integer.parseInt(kek[2]), y = Integer.parseInt(kek[3]), z = Integer.parseInt(kek[4]);
                int[] edges = new int[kek.length - 5];
                for (int i = 0; i < kek.length - 5; i++)
                    edges[i] = Integer.parseInt(kek[i + 5]);
                if (type.equals("object")) {
                    String[] data = interactDataFor(index);
                    super.addVertex(new ObjectVertex(index, x, y, z, edges, data[0], data[1]));
                }
                super.addVertex(new WebVertex(index, x, y, z, edges));
            }
            in.close();
            for (WebVertex vertex : this) {
                for (WebVertex edge : this) {
                    for (int edgeI : vertex.getEdgeIndexes()) {
                        if (edge.getIndex() == edgeI) {
                            vertex.getEdges().add(edge);
                            super.addEdge(vertex, edge);
                        }
                    }
                }
                vertex.calculateDistances();
                if (vertex.getEdges().size() != vertex.getEdgeIndexes().length) {
                    System.out.println("Something is fucked up with edges of vertex " + vertex.getIndex());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] interactDataFor(int vertex) {
        switch (vertex) {
            case 135:
                return new String[]{"Gate", "Pay-toll(10gp)"};
            case 310:
                return new String[]{"Door", "Open"};
            case 312:
                return new String[]{"Staircase", "Climb-down"};
            case 314:
                return new String[]{"Staircase", "Climb-up"};
            case 347:
                return new String[]{"Gate", "Open"};
            case 593:
            case 595:
                return new String[]{"Large door", "Open"};
            case 743:
            case 744:
                return new String[]{"Underwall tunnel", "Climb-into"};
            case 41:
            case 764:
                return new String[]{"Crumbling wall", "Climb-over"};
            case 767:
            case 768:
                return new String[]{"Gate", "Open"};
            case 338:
                return new String[]{"Trapdoor", "Climb-down"};
            case 336:
                return new String[]{"Ladder", "Climb-up"};
            case 770:
                return new String[]{"Ladder", "Climb-down"};
            case 803:
                return new String[]{"Ladder", "Climb-up"};
            case 629:
            case 700:
            case 714:
                return new String[]{"Gate", "Open"};
            default: {
                System.out.println("UNKNOWN OBJECT FOR VERTEX " + vertex);
                return new String[2];
            }
        }
    }

    public WebVertex getVertex(int idx) {
        for (WebVertex v : this) {
            if (v.getIndex() == idx)
                return v;
        }
        return null;
    }

    public WebVertex getNearestVertex(Locatable to) {
        WebVertex best = null;
        double dist = 69420;
        for (WebVertex vertex : this) {
            double distance = vertex.getTile().distance(to);
            if (distance < dist) {
                dist = distance;
                best = vertex;
            }
        }
        return best;
    }

    public WebPath findPathToBank(WebBank bank) {
        return WebPath.build(bank.getLocation());
    }

    public WebPath findPathToNearestBank() {
        return findPathToBank(getNearestBank());
    }

    public WebBank getNearestBank() {
        return WebBank.getNearest();
    }

    public WebBank getNearestBank(WebBank.Type type) {
        return WebBank.getNearest(t -> t.getType() == type);
    }

    public WebBank getNearestBank(Filter<WebBank> filter) {
        return WebBank.getNearest(filter);
    }

    public WebVertex getNearestVertex() {
        return getNearestVertex(Players.getLocal());
    }

    public DijkstraPathfinder getPathfinder() {
        return PATHFINDER;
    }
}
