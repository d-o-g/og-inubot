package com.inubot.api.methods.traversal.graph;

import com.inubot.Inubot;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.graph.data.ObjectVertex;
import com.inubot.api.methods.traversal.graph.data.WebVertex;
import com.inubot.api.oldschool.Locatable;
import com.inubot.api.util.Digraph;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Septron
 * @since July 01, 2015
 */
public class Web extends Digraph<WebVertex, WebVertex> {

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
            default: {
                Inubot.LOGGER.info("UNKNOWN OBJECT FOR VERTEX " + vertex);
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

    public WebVertex getNearestVertex() {
        return getNearestVertex(Players.getLocal());
    }
}
